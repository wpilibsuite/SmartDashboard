package edu.wpi.first.smartdashboard.robot;

import java.io.*;

import edu.wpi.first.wpilibj.networktables.*;
import edu.wpi.first.wpilibj.networktables2.client.*;
import edu.wpi.first.wpilibj.networktables2.stream.*;
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

	
	private static volatile String _host = null;
	private static volatile int _port = NetworkTable.DEFAULT_PORT;
	private static final IOStreamFactory configurableFactory = new IOStreamFactory() {
		@Override
		public IOStream createStream() throws IOException {
                    if(_host==null)
                        return null;
                    return new SocketStream(_host, _port);
		}
	};
	public static final NetworkTableClient client = new NetworkTableClient(configurableFactory);
	private static final NetworkTableProvider provider = new NetworkTableProvider(client);
        static{
            NetworkTable.setTableProvider(provider);
        }

	public static void setTeam(int team) {
		setHost("10." + (team / 100) + "." + (team % 100) + ".2");
	}
	public static void setHost(String host){
		_host = host;
		System.out.println("Host: "+host);
		client.close();
	}
	public static void setPort(int port){
		_port = port;
		client.close();
	}

	public static ITable getTable(String tableName) {
		return provider.getRootTable().getSubTable(tableName);
	}

	public static ITable getTable() {
		return provider.getRootTable().getSubTable(TABLE_NAME);
	}

	public static ITable getPreferences() {
		return provider.getRootTable().getSubTable(PREFERENCES_NAME);
	}
    public static ITable getLiveWindow() {
        return provider.getRootTable().getSubTable(LIVE_WINDOW_NAME);
    }
    
	public static void addConnectionListener(IRemoteConnectionListener listener, boolean immediateNotify) {
		client.addConnectionListener(listener, immediateNotify);
	}
	public static void removeConnectionListener(IRemoteConnectionListener listener) {
		client.removeConnectionListener(listener);
	}

}
