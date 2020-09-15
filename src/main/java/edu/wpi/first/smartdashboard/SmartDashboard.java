package edu.wpi.first.smartdashboard;

import edu.wpi.first.networktables.NetworkTablesJNI;
import edu.wpi.first.smartdashboard.extensions.FileSniffer;
import edu.wpi.first.smartdashboard.gui.DashboardFrame;
import edu.wpi.first.smartdashboard.properties.IntegerProperty;
import edu.wpi.first.smartdashboard.properties.StringProperty;
import edu.wpi.first.smartdashboard.robot.Robot;
import edu.wpi.first.wpiutil.CombinedRuntimeLoader;
import edu.wpi.first.wpiutil.WPIUtilJNI;

import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * SmartDashboard logic
 *
 * @author Joe Grinstead
 * @author pmalmsten
 */
public class SmartDashboard {

  /**
   * Variable used in the {@link SmartDashboard#inCompetition() inCompetition()} method
   */
  private static boolean inCompetition = false;

  /**
   * Returns whether or not this is in "competition" mode. Competition mode
   * should be used on the netbook provided for teams to use the dashboard. If
   * the SmartDashboard is in competition mode, then it automatically sizes
   * itself to be the standard dashboard size and to remove the frame around
   * it. It can be set to be in competition if "competition" is one of the
   * words passed in through the command line.
   *
   * @return whether or not this is in "competition" mode
   */
  public static boolean inCompetition() {
    return inCompetition;
  }


  private static DashboardFrame frame;

  /**
   * Starts the program
   *
   * @param args the standard arguments. If "competition" is one of them, then the SmartDashboard
   *             will be in competition mode
   * @see SmartDashboard#inCompetition() inCompetition()
   */
  public static void main(final String[] args) throws IOException {
    WPIUtilJNI.Helper.setExtractOnStaticLoad(false);
    NetworkTablesJNI.Helper.setExtractOnStaticLoad(false);
    CombinedRuntimeLoader.loadLibraries(SmartDashboard.class, "wpiutiljni", "ntcorejni");


    NetworkTablesJNI.getDefaultInstance();

    try {
      SwingUtilities.invokeAndWait(new Runnable() {
        public void run() {
          try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
          } catch (Exception e) {
            // TODO
          }
        }
      });
    } catch (Exception ex) {
      ex.printStackTrace();
      System.exit(2);
    }

    // Present a loading bar (it will only show up if this is going slowly)
    final ProgressMonitor monitor
        = new ProgressMonitor(null, "Loading SmartDashboard", "Initializing internal code...", 0,
        1000);

    // Search the filesystem for extensions (49%)
    FileSniffer.findExtensions(monitor, 0, 490);

    // Parse arguments
    ArgParser argParser = new ArgParser(args, true, true, new String[]{"ip"});
    inCompetition = argParser.hasFlag("competition");

    // Initialize GUI
    try {
      SwingUtilities.invokeAndWait(new Runnable() {
        public void run() {
          frame = new DashboardFrame(inCompetition);
        }
      });
    } catch (Exception ex) {
      ex.printStackTrace();
      System.exit(2);
    }

    if (argParser.hasValue("ip")) {
      monitor.setProgress(650);
      monitor.setNote("Connecting to robot at: " + argParser.getValue("ip"));
      Robot.setHost(argParser.getValue("ip"));
      System.out.println("IP: " + argParser.getValue("ip"));
    } else {
      monitor.setProgress(600);
      monitor.setNote("Getting Team Number");
      StringProperty teamProp = frame.getPrefs().team;
      String teamNumber = teamProp.getValue();

      teamNumberLoop:
      while (teamNumber.equals("0")) {
          String input = JOptionPane.showInputDialog("Input Team Number\\Host");
          if (input == null) {
            break teamNumberLoop;
          }
          teamNumber = input;
      }

      monitor.setProgress(650);
      monitor.setNote("Connecting to robot: " + teamNumber);
      Robot.setHost(teamNumber);
      teamProp.setValue(teamNumber);
    }

    try {
      SwingUtilities.invokeAndWait(new Runnable() {

        public void run() {
          try {
            frame.pack();
            frame.setVisible(true);

            monitor.setProgress(750);
            monitor.setNote("Loading From Save");

            // Load
            File file = new File(frame.getPrefs().saveFile.getValue());
            if (file.exists()) {
              frame.load(file.getPath());
            }

            monitor.setProgress(1000);

          } catch (Exception e) {
            e.printStackTrace();

            System.exit(1);
          }
        }
      });
    } catch (Exception ex) {
      ex.printStackTrace();
      System.exit(2);
    }
  }
}
