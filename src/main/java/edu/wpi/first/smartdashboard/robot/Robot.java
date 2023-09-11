package edu.wpi.first.smartdashboard.robot;

import java.util.function.Consumer;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEvent;
import edu.wpi.first.networktables.NetworkTableInstance;

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
  private static final NetworkTableInstance ntInstance = NetworkTableInstance.getDefault();

  static {
    ntInstance.stopDSClient();
    ntInstance.startClient4(identity); 
  }

  public static void startDSClient() {
    ntInstance.startDSClient();
  }

  public static void setTeam(int team) {
    _host = "roboRIO-" + team + "-FRC.local";
    ntInstance.setServerTeam(team);
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
      ntInstance.setServer(host);
    }
  }

  public static String getHost() {
    return _host;
  }


  public static NetworkTable getTable(String tableName) {
    return ntInstance.getTable(tableName);
  }

  public static NetworkTable getTable() {
    return ntInstance.getTable(TABLE_NAME);
  }

  public static NetworkTable getPreferences() {
    return ntInstance.getTable(PREFERENCES_NAME);
  }

  public static NetworkTable getLiveWindow() {
    return ntInstance.getTable(LIVE_WINDOW_NAME);
  }

  public static int addConnectionListener(Consumer<NetworkTableEvent> listener, boolean
      immediateNotify) {
    System.out.println("Adding connection listener");
    return ntInstance.addConnectionListener(true, listener);

  }

  public static void removeConnectionListener(int listenerHandle) {
    System.out.println("Removing connection listener");
    ntInstance.removeListener(listenerHandle);
  }

}
