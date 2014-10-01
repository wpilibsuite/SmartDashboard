package edu.wpi.first.smartdashboard.gui;

import edu.wpi.first.smartdashboard.livewindow.elements.LWSubsystem;
import edu.wpi.first.smartdashboard.livewindow.elements.Controller;
import edu.wpi.first.smartdashboard.robot.Robot;
import edu.wpi.first.smartdashboard.types.DisplayElementRegistry;
import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.first.wpilibj.tables.ITableListener;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Set;
import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import org.jfree.ui.ExtensionFileFilter;

/**
 * This is the menu bar on top of the window. It can be set to hide
 * automatically in the preferences.
 *
 * @author Joe Grinstead
 */
public class DashboardMenu extends JMenuBar {

    /**
     * Creates a menu for the given panel.
     *
     */
    DashboardMenu(final DashboardFrame frame, final MainPanel mainPanel) {
        JMenu fileMenu = new JMenu("File");
        JMenuItem loadMenu = new JMenuItem("Open...");
        loadMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
        loadMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                fc.setCurrentDirectory(new File(frame.getPrefs().saveFile.getValue()));
                fc.addChoosableFileFilter(new ExtensionFileFilter("XML File", ".xml"));
                fc.setMultiSelectionEnabled(false);
                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fc.setApproveButtonText("Open");
                fc.setDialogTitle("Open");

                if (fc.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                    String filepath = fc.getSelectedFile().getAbsolutePath();

                    frame.load(filepath);
                    frame.getPrefs().saveFile.setValue(filepath);
                }
            }
        });
        fileMenu.add(loadMenu);

        JMenuItem newMenu = new JMenuItem("New");
        newMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));
        newMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mainPanel.getPanel("SmartDashboard").clear();
            }
        });
        fileMenu.add(newMenu);

        JMenuItem saveMenu = new JMenuItem("Save");
        saveMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
        saveMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.save(frame.getPrefs().saveFile.getValue());
            }
        });
        fileMenu.add(saveMenu);

        JMenuItem saveAs = new JMenuItem("Save As...");
        saveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK));
        saveAs.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser(".");
                fc.addChoosableFileFilter(new ExtensionFileFilter("XML File", ".xml"));
                fc.setApproveButtonText("Save");
                fc.setDialogTitle("Save As...");

                fc.setMultiSelectionEnabled(false);
                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

                if (fc.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                    String filepath = fc.getSelectedFile().getAbsolutePath();
                    if (!filepath.endsWith(".xml")) {
                        filepath = filepath + ".xml";
                    }
                    frame.save(filepath);
                    frame.getPrefs().saveFile.setValue(filepath);
                }
            }
        });
        fileMenu.add(saveAs);

        JMenuItem prefMenu = new JMenuItem("Preferences");
        prefMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PropertyEditor editor = frame.getPropertyEditor();
                editor.setPropertyHolder(frame.getPrefs());
                editor.setTitle("Edit Preferences");
                editor.setVisible(true);
            }
        });
        fileMenu.add(prefMenu);

        JMenuItem exitMenu = new JMenuItem("Exit");
        exitMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.exit();
            }
        });
        fileMenu.add(exitMenu);

        JMenu viewMenu = new JMenu("View");
        final JCheckBoxMenuItem editMode = new JCheckBoxMenuItem("Editable");
        editMode.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for (DashboardPanel panel : MainPanel.panels.values()) {
                    panel.setEditable(!panel.isEditable());
                }
            }
        });
        editMode.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.CTRL_DOWN_MASK));
        viewMenu.add(editMode);

        JCheckBoxMenuItem editSystems = new JCheckBoxMenuItem("Edit Subsystems");
        editSystems.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                LWSubsystem.setEditable(!LWSubsystem.isEditable());
            }
        });
        editSystems.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK));
        editSystems.doClick();
        viewMenu.add(editSystems);

        final JMenuItem resetLW = new JMenuItem("Reset LiveWindow");
        resetLW.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for (LWSubsystem subsystem : MainPanel.getPanel("LiveWindow").getSubsystems()) {
                    for (Widget component : subsystem.getWidgets()) {
                        if (component instanceof Controller) {
                            System.out.println("\tResetting " + component.getFieldName());
                            ((Controller) component).reset();
                        }
                    }
                }
            }
        });
        resetLW.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK));
        Robot.getLiveWindow().getSubTable("~STATUS~").addTableListener("LW Enabled", new ITableListener() {
            public void valueChanged(ITable itable, String string, Object o, boolean bln) {
                final boolean isInLW = Robot.getLiveWindow().getSubTable("~STATUS~").getBoolean("LW Enabled", false);

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        frame.setDisplayMode(isInLW
                                ? DashboardFrame.DisplayMode.LiveWindow
                                : DashboardFrame.DisplayMode.SmartDashboard);

                        mainPanel.setCurrentPanel(isInLW
                                ? MainPanel.getPanel("LiveWindow")
                                : MainPanel.getPanel("SmartDashboard"));
                        if (!isInLW) {
                            resetLW.doClick();
                        }

                    }
                });
            }
        }, true);
        viewMenu.add(resetLW);

        JMenu addMenu = new JMenu("Add...");
        Set<Class<? extends StaticWidget>> panels = DisplayElementRegistry.getStaticWidgets();
        for (final Class<? extends StaticWidget> option : panels) {
            JMenuItem item = new JMenuItem(DisplayElement.getName(option));
            item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        StaticWidget element = option.newInstance();
                        mainPanel.getPanel("SmartDashboard").addElement(element, null);
                    } catch (InstantiationException ex) {
                    } catch (IllegalAccessException ex) {
                    }
                }
            });

            addMenu.add(item);
        }

        viewMenu.add(addMenu);

        final JMenu revealMenu = new JMenu("Reveal...");

        viewMenu.addMenuListener(new MenuListener() {
            public void menuSelected(MenuEvent e) {
                revealMenu.removeAll();

                int count = 0;
                for (final String field : mainPanel.getPanel("SmartDashboard").getHiddenFields()) {
                    if (mainPanel.getPanel("SmartDashboard").getTable().containsKey(field)) {
                        count++;
                        revealMenu.add(new JMenuItem(new AbstractAction(field) {
                            public void actionPerformed(ActionEvent e) {
                                mainPanel.getPanel("SmartDashboard").addField(field);
                            }
                        }));
                    }
                }

                revealMenu.setEnabled(count != 0);
            }

            public void menuDeselected(MenuEvent e) {
            }

            public void menuCanceled(MenuEvent e) {
            }
        });

        viewMenu.add(revealMenu);

        JMenuItem removeUnusedMenu = new JMenuItem("Remove Unused");
        removeUnusedMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MainPanel.getCurrentPanel().removeUnusedFields();
            }
        });

        viewMenu.add(removeUnusedMenu);

        add(fileMenu);
        add(viewMenu);
    }
}
