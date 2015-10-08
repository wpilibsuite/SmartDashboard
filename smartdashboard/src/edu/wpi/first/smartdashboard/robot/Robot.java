package edu.wpi.first.smartdashboard.robot;

import java.io.*;

import edu.wpi.first.wpilibj.networktables.*;
import edu.wpi.first.wpilibj.tables.*;

/**
 * 
 * @author Joe
 */
public class Robot {

	public static final String PREF_SAVE_FIELD = "~S A V E~";
	public static final String TABLE_NAME = "SmartDashboard";
    public static final String LIVE_WINDOW_NAME = "LiveWindow";
	public static final String PREFERENCES_NAME = "Preferences";
	public static final String identity = "SmartDashboard";
	
	private static volatile String _host = "";
	private static volatile int _port = NetworkTable.DEFAULT_PORT;
	private static int _team;
	private static boolean _usemDNS = true;
        static{
            NetworkTable.setClientMode();
        }

	public static void setTeam(int team) {
		_team = team;
		setHost("");
	}
	
	/**
	 * Switch between using MDNS and a static IP to resolve the robot's
	 * hostname (mDNS is only supported on the roboRIO)
	 * @param useMDNS
	 */
	public static void setUseMDNS(boolean usemDNS) {
		_usemDNS = usemDNS;
		setHost("");
	}

    public static void setHost(String host) {
        if (host != "") {
            // Use the given host
		} else if (_usemDNS) {
			host = "roboRIO-" + _team + "-frc.local";
		} else {
			host = "10." + (_team / 100) + "." + (_team % 100) + ".2";
		}
		if (_host.equals(host)) return;
		_host = host;
		System.out.println("Host: "+host);
		try {
			NetworkTable.shutdown();
		} catch (IllegalStateException ex) {
		}
		NetworkTable.setIPAddress(host);
		NetworkTable.setNetworkIdentity(identity);
		NetworkTable.initialize();
	}
	
	public static String getHost() {
		return _host;
	}
	
	public static void setPort(int port){
		if (_port == port) return;
		_port = port;
		try {
			NetworkTable.shutdown();
		} catch (IllegalStateException ex) {
		}
		NetworkTable.setPort(port);
		NetworkTable.initialize();
	}

	public static ITable getTable(String tableName) {
		return NetworkTable.getTable(tableName);
	}

	public static ITable getTable() {
		return NetworkTable.getTable(TABLE_NAME);
	}

	public static ITable getPreferences() {
		return NetworkTable.getTable(PREFERENCES_NAME);
	}
    public static ITable getLiveWindow() {
        return NetworkTable.getTable(LIVE_WINDOW_NAME);
    }
    
	public static void addConnectionListener(IRemoteConnectionListener listener, boolean immediateNotify) {
	    System.out.println("Adding connection listener");
		NetworkTable.getTable(TABLE_NAME).addConnectionListener(listener, immediateNotify);
	}
	public static void removeConnectionListener(IRemoteConnectionListener listener) {
	    System.out.println("Removing connection listener");
		NetworkTable.getTable(TABLE_NAME).removeConnectionListener(listener);
	}

}
