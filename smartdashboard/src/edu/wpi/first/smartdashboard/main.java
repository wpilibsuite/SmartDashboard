package edu.wpi.first.smartdashboard;

import java.io.*;

import javax.swing.*;

import edu.wpi.first.smartdashboard.extensions.*;
import edu.wpi.first.smartdashboard.gui.*;
import edu.wpi.first.smartdashboard.properties.*;
import edu.wpi.first.smartdashboard.robot.*;

/**
 * main SmartDashboard logic
 *
 * @author Joe Grinstead
 * @author pmalmsten
 */
public class main {
    
    /** Variable used in the {@link main#inCompetition() inCompetition()} method */
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
     * @param args
     *            the standard arguments. If "competition" is one of them, then
     *            the SmartDashboard will be in competition mode
     * @see main#inCompetition() inCompetition()
     */
    public static void main(final String[] args) {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    try {
                        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    } catch (Exception e) {}
            }});
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(2);
        }
        
        // Present a loading bar (it will only show up if this is going slowly)
        final ProgressMonitor monitor = new ProgressMonitor(null, "Loading SmartDashboard", "Initializing internal code...", 0, 1000);
        
        // Search the filesystem for extensions (49%)
        FileSniffer.findExtensions(monitor, 0, 490);
        
        
        
        ArgParser argParser = new ArgParser(args, true, true, new String[] { "ip" });
        inCompetition = argParser.hasFlag("competition");
        
        
        
        
        
        // Initialize GUI
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    frame = new DashboardFrame(inCompetition);
            }});
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(2);
        }
        
        
        
        
        IntegerProperty teamProp = frame.getPrefs().team;
        if (argParser.hasValue("ip")) {
            monitor.setProgress(650);
            monitor.setNote("Connecting to robot at: "+argParser.getValue("ip"));
            Robot.setHost(argParser.getValue("ip"));
        }
        else{
            monitor.setProgress(600);
            monitor.setNote("Getting Team Number");
            int teamNumber = teamProp.getValue();
            teamNumberLoop: while (teamNumber <= 0) {
                try{
					String input = JOptionPane.showInputDialog("Input Team Number");
					if(input==null){
						teamNumber = 0;
						break teamNumberLoop;
					}
                    teamNumber = Integer.parseInt(input);
                } catch(Exception e){}
            }
            monitor.setProgress(650);
            monitor.setNote("Connecting to robot of team: "+teamNumber);
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