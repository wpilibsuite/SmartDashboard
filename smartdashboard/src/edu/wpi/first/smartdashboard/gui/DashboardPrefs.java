package edu.wpi.first.smartdashboard.gui;

import edu.wpi.first.smartdashboard.main;
import java.util.*;
import java.util.prefs.*;

import javax.swing.*;

import edu.wpi.first.smartdashboard.properties.*;
import edu.wpi.first.smartdashboard.robot.*;
import java.io.File;

/**
 * 
 * @author brad
 */
public class DashboardPrefs implements PropertyHolder {
    private static final File USER_HOME = new File(System.getProperty("user.home"));
    private static final File USER_SMARTDASHBOARD_HOME = new File(USER_HOME, "SmartDashboard");
    static{
        if(!USER_SMARTDASHBOARD_HOME.exists()){
            USER_SMARTDASHBOARD_HOME.mkdirs();
        }
    }

	private Map<String, Property> properties = new LinkedHashMap<String, Property>();
	public final IntegerProperty team = new IntegerProperty(this, "Team Number", 0);
	public final BooleanProperty hideMenu = new BooleanProperty(this, "Hide Menu", false);
	public final BooleanProperty autoShowWidgets = new BooleanProperty(this, "Automatically Show Widgets", true);
	public final IntegerListProperty grid_widths = new IntegerListProperty(this, "Grid Cell Width(s)", new int[] { 16 });
	public final IntegerListProperty grid_heights = new IntegerListProperty(this, "Grid Cell Height(s)", new int[] { 16 });
	public final IntegerProperty x = new IntegerProperty(this, "Window X Position", 0);
	public final IntegerProperty y = new IntegerProperty(this, "Window Y Position", 0);
	public final IntegerProperty width = new IntegerProperty(this, "Window Width", 640);
	public final IntegerProperty height = new IntegerProperty(this, "Window Height", 480);
	public final FileProperty saveFile = new FileProperty(this, "Save File", new File(USER_SMARTDASHBOARD_HOME, "save.xml").getAbsolutePath());
	public final BooleanProperty logToCSV = new BooleanProperty(this, "Log to CSV", false);
	public final FileProperty csvFile = new FileProperty(this, "CSV File", new File(USER_SMARTDASHBOARD_HOME, "csv.txt").getAbsolutePath());
	private Preferences node;
	
        
        public static DashboardPrefs getInstance(){
            return DashboardFrame.getInstance().getPrefs();
        }
	
	private final DashboardFrame frame;
	public DashboardPrefs(DashboardFrame frame) {
		this.frame = frame;
		node = Preferences.userNodeForPackage(main.class);

		for (Property property : properties.values()) {
			if (property == logToCSV) {//always set logtoCSV to default on load
				continue;
			}
			load(property);
		}
	}

	private void load(Property property) {
		property.setSaveValue(node.get(property.getName(), property.getSaveValue()));
	}

	public Map<String, Property> getProperties() {
		return properties;
	}

	public boolean validatePropertyChange(Property property, Object value) {
		if (property == team || property == width || property == height) {
			return (Integer) value > 0;
		} else if (property == grid_widths || property == grid_heights) {
			int[] values = (int[]) value;

			if (values.length == 0) {
				return false;
			} else {
				for (int i : values) {
					if (i <= 0) {
						return false;
					}
				}
				return true;
			}
		} else if (property == logToCSV) {
			if ((Boolean) value) {
				int result = JOptionPane.showOptionDialog(null, "Should SmartDashboard start logging to the CSV file? (This will override the existing file)",
						"Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, false);
				return result == JOptionPane.YES_OPTION;
			}
		}
		return true;
	}

	public void propertyChanged(Property property) {
		node.put(property.getName(), property.getSaveValue());

		if (property == x) {
			frame.setLocation(x.getValue(), frame.getY());
		} else if (property == y) {
			frame.setLocation(frame.getX(), y.getValue());
		} else if (property == width) {
			frame.setSize(width.getValue(), frame.getHeight());
		} else if (property == height) {
			frame.setSize(frame.getWidth(), height.getValue());
		} else if (property == team) {
			Robot.setTeam(team.getValue());
			frame.setTitle("SmartDashboard - " + team.getValue());
		} else if (property == hideMenu) {
			frame.setShouldHideMenu(hideMenu.getValue());
		} else if (property == logToCSV) {
			if (logToCSV.getValue()) {
				frame.getLogger().start(csvFile.getValue());
			} else {
				frame.getLogger().stop();
			}
		}
	}
}
