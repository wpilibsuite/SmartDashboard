package edu.wpi.first.smartdashboard;

import edu.wpi.first.networktables.EntryListenerFlags;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableValue;
import edu.wpi.first.networktables.TableEntryListener;
import edu.wpi.first.smartdashboard.gui.DashboardFrame;
import edu.wpi.first.smartdashboard.robot.Robot;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JOptionPane;

/**
 * Logs all information received to a CSV file.
 *
 * @author pmalmsten
 */
public class LogToCSV implements TableEntryListener {

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
        listenerHandle = Robot.getTable().addEntryListener(this,
            EntryListenerFlags.kImmediate | EntryListenerFlags.kLocal
          | EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);
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
    Robot.getTable().removeTableListener(listenerHandle);
    m_fw = null;
  }

  @Override
  public void valueChanged(NetworkTable source, String key, 
                           NetworkTableEntry entry, NetworkTableValue value, int flags) {
    if (m_fw != null) {
      try {
        long timeStamp = System.currentTimeMillis() - m_startTime;
        m_fw.write(timeStamp + "," + "\"" + key + "\"," + "\"" + value + "\"" + s_lineSeparator);
        m_fw.flush();
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }
  }
}
