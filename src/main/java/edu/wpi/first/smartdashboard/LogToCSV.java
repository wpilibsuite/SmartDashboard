package edu.wpi.first.smartdashboard;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEvent;
import edu.wpi.first.networktables.NetworkTable.TableEventListener;
import edu.wpi.first.smartdashboard.gui.DashboardFrame;
import edu.wpi.first.smartdashboard.robot.Robot;
import java.io.FileWriter;
import java.io.IOException;
import java.util.EnumSet;

import javax.swing.JOptionPane;

/**
 * Logs all information received to a CSV file.
 *
 * @author pmalmsten
 */
public class LogToCSV implements TableEventListener {

  private static final String s_lineSeparator = System.getProperty("line.separator");
  private long m_startTime;
  private FileWriter m_fw;
  private final DashboardFrame frame;
  private int listenerHandle;

  public LogToCSV(DashboardFrame frame) {
    this.frame = frame;
  }

  /*
   * Prepares this LogToCSV object to begin writing to the specified file. The
   * specified file is opened in write mode (any existing content is blown
   * away).
   *
   * @param path The path of the CSV file to write to.
   */
  public void start(String path) {
    if (m_fw == null) {
      try {
        m_startTime = System.currentTimeMillis();
        m_fw = new FileWriter(path);
        m_fw.write("Time (ms),Name,Value" + s_lineSeparator);
        m_fw.flush();
        listenerHandle = Robot.getTable().addListener(
            EnumSet.of(NetworkTableEvent.Kind.kImmediate, NetworkTableEvent.Kind.kValueAll,
                       NetworkTableEvent.Kind.kPublish),
            this);
      } catch (IOException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null,
            "An error occurred when attempting to " + "open the output CSV file for writing. "
                + "Please check the file path preference.", "Unable to Open CSV File",
            JOptionPane.ERROR_MESSAGE);
        frame.getPrefs().logToCSV.setValue(false);
      }
    }
  }

  /*
   * If logging was previously enabled, this method flushes and releases the
   * file handle to the CSV file. Logging will no longer occur.
   */
  public void stop() {
    if (m_fw == null) {
      return;
    }

    try {
      m_fw.close();
    } catch (IOException ex) {
      ex.printStackTrace();
    }
    Robot.getTable().removeListener(listenerHandle);
    m_fw = null;
  }

  @Override
  public void accept(NetworkTable source, String key, 
                     NetworkTableEvent event) {
    if (m_fw != null) {
      try {
        long timeStamp = System.currentTimeMillis() - m_startTime;
        m_fw.write(timeStamp + "," + "\"" + key + "\"," + "\"" + event.valueData
            .value.getValue() + "\"" + s_lineSeparator);
        m_fw.flush();
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }
  }
}
