package edu.wpi.first.smartdashboard.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

import javax.swing.*;

import edu.wpi.first.smartdashboard.*;
import edu.wpi.first.smartdashboard.gui.elements.bindings.AbstractTableWidget;
import edu.wpi.first.smartdashboard.livewindow.elements.LWSubsystem;
import edu.wpi.first.smartdashboard.properties.*;
import edu.wpi.first.smartdashboard.robot.Robot;
import edu.wpi.first.smartdashboard.types.*;
import edu.wpi.first.smartdashboard.xml.*;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.tables.ITable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class defines the main window for the FRC program. It contains almost no
 * logic except for the {@link DashboardFrame#load(java.lang.String) load(...)}
 * and {@link DashboardFrame#save(java.lang.String) save(...)} method.
 * 
 * @author Joe Grinstead
 */
public class DashboardFrame extends JFrame {

	/*
	 * If the menu bar is set to "hidden," then this defines what portion of the
	 * top screen is reserved for revealing the menu bar when the mouse moves
	 * over it
	 */
	private static final int MENU_HEADER = 10;
	/** The size the frame should be when displayed on the netbook */
	private static final Dimension NETBOOK_SIZE = new Dimension(1024, 400);
	/** The minimum size of the frame */
	private static final Dimension MINIMUM_SIZE = new Dimension(300, 200);
	
    public enum DisplayMode {
        SmartDashboard,
        LiveWindow;
    }
	
	private final DashboardPrefs prefs = new DashboardPrefs(this);
	/** The content of the frame */
	private final DashboardPanel smartDashboardPanel;
    private final DashboardPanel liveWindowPanel;
    private final MainPanel mainPanel;
    private DisplayMode displayMode = DisplayMode.SmartDashboard;
	/** The menu bar */
	private final JMenuBar menuBar;
	/** The property table (there is only this one ever) */
	private final PropertyEditor propEditor;
	/** Whether or not the menu bar should be hidden */
	private boolean shouldHideMenu = prefs.hideMenu.getValue();
    
    private static final String LW_SAVE = "_"+Robot.getLiveWindow().getSubTable("~STATUS~").getString("Robot", "LiveWindow")+".xml";
	
	private final LogToCSV logger = new LogToCSV(this);
         
        private static DashboardFrame INSTANCE = null;
        public static DashboardFrame getInstance(){
            return INSTANCE;
        }

	/**
	 * Initializes the frame.
	 * 
	 * @param competition
	 *            whether or not to display as though it were on the netbook
	 */
	public DashboardFrame(boolean competition) {
		super("SmartDashboard - ");

		setLayout(new BorderLayout());

		// The content of the frame is really contained in the panel and menu
		smartDashboardPanel = new DashboardPanel(this, Robot.getTable());
        smartDashboardPanel.setName("SmartDashboard");
        liveWindowPanel = new DashboardPanel(this, Robot.getLiveWindow());
        liveWindowPanel.setName("LiveWindow");
        mainPanel = new MainPanel(new CardLayout(), smartDashboardPanel, liveWindowPanel, smartDashboardPanel);
        mainPanel.add(smartDashboardPanel, DisplayMode.SmartDashboard.toString());
        mainPanel.add(liveWindowPanel, DisplayMode.LiveWindow.toString());
        setDisplayMode(DisplayMode.SmartDashboard);
		menuBar = new DashboardMenu(this, mainPanel);
		propEditor = new PropertyEditor(this);

		if (!shouldHideMenu) {
			add(menuBar, BorderLayout.NORTH);
		}
		add(mainPanel, BorderLayout.CENTER);

		// Look for when the menu bar should be displayed
		MouseAdapter hideListener = new MouseAdapter() {

			@Override
			public void mouseMoved(MouseEvent e) {
				if (shouldHideMenu && e.getY() < MENU_HEADER) {
					add(menuBar, BorderLayout.NORTH);
					validate();
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				if (shouldHideMenu) {
					remove(menuBar);
					validate();
				}
			}
		};
		smartDashboardPanel.addMouseListener(hideListener);
		smartDashboardPanel.addMouseMotionListener(hideListener);

		// Set the size / look
		if (competition) {
			setPreferredSize(NETBOOK_SIZE);
			setUndecorated(true);
			setLocation(0, 0);
			setResizable(false);
		} else {
			setMinimumSize(MINIMUM_SIZE);

			// Closing operation is handled manually
			setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

			setPreferredSize(new Dimension(prefs.width.getValue(), prefs.height.getValue()));
			setLocation(prefs.x.getValue(), prefs.y.getValue());
		}

		// Call our own exit function when the frame is closed
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				exit();
			}
		});

		// Make resizing affect the preference variables for window size
		addComponentListener(new ComponentListener() {

			public void componentResized(ComponentEvent e) {
				prefs.width.setValue(getWidth());
				prefs.height.setValue(getHeight());
			}

			public void componentMoved(ComponentEvent e) {
				prefs.x.setValue(getX());
				prefs.y.setValue(getY());
			}

			public void componentShown(ComponentEvent e) {
			}

			public void componentHidden(ComponentEvent e) {
			}
		});
                
                INSTANCE = this;//will only be instanciated once so make this a singleton so plugins can get it for compatability
	}
    
    /**
     * Sets the current DisplayMode to the given mode.
     * @param mode The mode to put the frame into.
     */
    public final void setDisplayMode(DisplayMode mode) {
        displayMode = mode;
        CardLayout cl = (CardLayout)(mainPanel.getLayout());
        cl.show(mainPanel, mode.toString());
    }

	/**
	 * Returns the property editor
	 * 
	 * @return the property editor
	 */
	public PropertyEditor getPropertyEditor() {
		return propEditor;
	}

	/**
	 * Sets whether or not the menu should be hidden. This does not attempt to
	 * change the property setting, instead the property setting should call
	 * this.
	 * 
	 * @param shouldHide
	 *            whether or not the menu should hide
	 */
	public void setShouldHideMenu(boolean shouldHide) {
		if (shouldHideMenu != shouldHide) {
			shouldHideMenu = shouldHide;
			if (shouldHideMenu) {
				remove(menuBar);
			} else {
				add(menuBar, BorderLayout.NORTH);
			}
			validate();
		}
	}

	/**
	 * Saves the frame to the file of the given name
	 * 
	 * @param path
	 *            the path to save the file to
	 */
	public void save(String path) {
        try {
            System.out.println("Saving to:\t"+path);
            SmartDashboardXMLWriter writer = new SmartDashboardXMLWriter(path);

            writer.beginSmartDashboard();
            saveElements(writer, smartDashboardPanel);
            writer.endSmartDashboard();
            
            writer.beginLiveWindow();
            saveElements(writer, liveWindowPanel);
            writer.endLiveWindow();

            for (String field : smartDashboardPanel.getHiddenFields()) {
                writer.addHiddenField(field);
            }

            writer.close();
        } catch (Exception ex) {
            Logger.getLogger(DashboardFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
	}
    
    private void saveElements(SmartDashboardXMLWriter writer, DashboardPanel toSave) throws IOException {
        for (DisplayElement element : toSave.getElements()) {
            boolean isWidget = element instanceof Widget;
            assert isWidget || element instanceof StaticWidget;
            if (isWidget) {
                writer.beginWidget(((Widget) element).getFieldName(), ((Widget) element).getType().getName(), element.getClass().getName());
            } else {
                writer.beginStaticWidget(element.getClass().getName());
            }
            if(element instanceof LWSubsystem) {
                for(Widget w : ((LWSubsystem) element).getWidgets()) {
                    System.out.println("   Saving "+((LWSubsystem)element).getFieldName()+"|"+w.getFieldName());
                    writer.addSubWidget(w.getFieldName(), w.getType().getName(), w.getClass().getName());
                    writer.addSubWidgetLocation(w.getLocation());
                    writer.addSubWudgetHeight(w.getHeight());
                    writer.addSubWidgetWidth(w.getWidth());
                    writer.endSubWidget();
                }
            }
            writer.addLocation(element.getLocation());

            Dimension size = element.getSavedSize();
            if (size.width > 0) {
                writer.addWidth(size.width);
            }
            if (size.height > 0) {
                writer.addHeight(size.height);
            }

            for (Map.Entry<String, Property> prop : element.getProperties().entrySet()) {
                if (!prop.getValue().isDefault()) {
                    writer.addProperty(prop.getKey(), prop.getValue().getSaveValue());
                }
            }

            if (isWidget) {
                writer.endWidget();
            } else {
                writer.endStaticWidget();
            }
        }
    }

	/**
	 * Loads the current state of this MainWindow and any significant objects it
	 * contains
	 */
	public void load(String path) {
		try {
			SmartDashboardXMLReader reader = new SmartDashboardXMLReader(path);
            
            System.out.println("\nLoading from \t"+path);

			List<XMLWidget> sdWidgets = reader.getXMLWidgets();
			for (int i = sdWidgets.size(); i > 0; i--) {
                System.out.println("Loading SmartDashboard widgets....");
				XMLWidget widget = sdWidgets.get(i - 1);
				DisplayElement element = widget.convertToDisplayElement();
				if (element instanceof Widget) {
					Widget e = (Widget) element;
					Object value = null;
                    if (Robot.getTable().containsKey(e.getFieldName())) {
                        value = Robot.getTable().getValue(e.getFieldName());
                        DataType type = DataType.getType(value);
                        if (DisplayElementRegistry.supportsType(e.getClass(), type)) {
                            smartDashboardPanel.setField(e.getFieldName(), e, type, value, e.getSavedLocation());
                        }
                    } else {
                        smartDashboardPanel.setField(e.getFieldName(), e, widget.getType(), null, e.getSavedLocation());
                    }
				} else if (element instanceof StaticWidget) {
					StaticWidget e = (StaticWidget) element;
					smartDashboardPanel.addElement(e, widget.getLocation());
				} else {
					// TODO tell the user it was null
				}
			}
            
            LWSubsystem mostRecentParent = null;
            for(XMLWidget subsys : reader.getSubsystems().keySet()) {
                System.out.println("\nLoading \""+subsys.getField()+"\"");
                LWSubsystem subsystem = (LWSubsystem) subsys.convertToDisplayElement();
                mostRecentParent = subsystem;
                Object value1 = null;
                if(Robot.getLiveWindow().containsKey(subsystem.getFieldName())) {
                    value1 = Robot.getTable().getValue(subsystem.getFieldName());
                    DataType type = DataType.getType(value1);
                    if(DisplayElementRegistry.supportsType(subsystem.getClass(), type)) {
                        liveWindowPanel.setField(subsystem.getFieldName(), subsystem, type, value1, subsystem.getSavedLocation());
                    }
                } else {
                    liveWindowPanel.setField(subsystem.getFieldName(), subsystem, subsystem.getType(), null, subsystem.getSavedLocation());
                }
                for(XMLWidget component : reader.getSubwidgetMap(subsys).values()) {
                    System.out.println("Adding subcomponent \""+component.getField()+"\"");
                    AbstractTableWidget w = (AbstractTableWidget) component.convertToDisplayElement();
                    Object value2 = null;
                    value2 = Robot.getLiveWindow().getSubTable(mostRecentParent.getFieldName()).getSubTable(w.getFieldName());
                    DataType type = DataType.getType(value2);
                    mostRecentParent.addWidget(w);
                    w.setField(w.getFieldName(), w, type, value2, mostRecentParent, w.getSavedLocation());
                    mostRecentParent.setSize(mostRecentParent.getPreferredSize());
                }
            }
            

			for (String field : reader.getHiddenFields()) {
				smartDashboardPanel.removeField(field);
			}

			Map<String, Property> prefMap = prefs.getProperties();
			for (Map.Entry<String, String> entry : reader.getProperties().entrySet()) {
				Property prop = prefMap.get(entry.getKey());
				prop.setValue(entry.getValue());
			}

			repaint();
		} catch (FileNotFoundException e) {
			// TODO tell the user
		}
	}

	/**
	 * Exits the program, prompting the user to save.
	 */
	public void exit() {
		int result = JOptionPane.showConfirmDialog(this, new String[] { "Do you wish to save this layout?" }, "Save before quitting?",
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
		switch (result) {
		case JOptionPane.YES_OPTION:
			save(prefs.saveFile.getValue());
		case JOptionPane.NO_OPTION:
			System.exit(0);
		default: // Do Nothing (they called cancel)
		}
	}

	public DashboardPrefs getPrefs() {
		return prefs;
	}

	public LogToCSV getLogger() {
		return logger;
	}
}
