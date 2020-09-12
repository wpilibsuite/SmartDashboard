package edu.wpi.first.smartdashboard.robot;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.tables.IRemoteConnectionListener;
import edu.wpi.first.wpilibj.tables.ITable;

/**
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

  static {
    NetworkTable.setClientMode();
    NetworkTable.setNetworkIdentity(identity);
    NetworkTable.initialize();
  }

  public static void setTeam(int team) {
    _host = "roboRIO-" + team + "-FRC.local";
    NetworkTable.setTeam(team);
  }

  public static void setHost(String host) {
    if (host.matches("^\\d{1,5}$") ) { //matches team number 5 digits or less
      try {
        int teamNumber = Integer.parseInt(host);
        setTeam(teamNumber);
      } catch (NumberFormatException ex) {
        //should not get here, protected by regex
        ex.printStackTrace();
      }
    } else {
      _host = host;
      System.out.println("Host: " + host);
      NetworkTable.setIPAddress(host);
    }
  }

  public static String getHost() {
    return _host;
  }

  public static void setPort(int port) {
    if (_port == port) {
      return;
    }
    _port = port;
    try {
      NetworkTable.shutdown();
    } catch (IllegalStateException ex) {
      // TODO
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

  public static void addConnectionListener(IRemoteConnectionListener listener, boolean
      immediateNotify) {
    System.out.println("Adding connection listener");
    NetworkTable.getTable(TABLE_NAME).addConnectionListener(listener, immediateNotify);
  }

  public static void removeConnectionListener(IRemoteConnectionListener listener) {
    System.out.println("Removing connection listener");
    NetworkTable.getTable(TABLE_NAME).removeConnectionListener(listener);
  }

}
