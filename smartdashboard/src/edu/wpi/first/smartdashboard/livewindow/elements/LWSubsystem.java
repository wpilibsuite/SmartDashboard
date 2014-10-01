package edu.wpi.first.smartdashboard.livewindow.elements;

import edu.wpi.first.smartdashboard.gui.MainPanel;
import edu.wpi.first.smartdashboard.gui.Widget;
import edu.wpi.first.smartdashboard.gui.elements.bindings.AbstractTableWidget;
import edu.wpi.first.smartdashboard.properties.Property;
import edu.wpi.first.smartdashboard.types.DataType;
import edu.wpi.first.smartdashboard.types.named.LWSubsystemType;
import edu.wpi.first.smartdashboard.xml.SmartDashboardXMLReader;
import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.first.wpilibj.tables.ITableListener;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.SwingUtilities;

/**
 * The main player in the Live Window. Subsystems hold every component that
 * corresponds to that physical subsystem on the robot (e.g. a drive train would
 * contain speed controllers and encoders).
 *
 * @author Sam Carlberg
 */
public class LWSubsystem extends AbstractTableWidget {

    public static final DataType[] TYPES = {LWSubsystemType.get()};
    private BoxLayout layout;
    private Dimension preferredSize = new Dimension(100, 100);
    /**
     * An implementation of {@link MouseAdapter} that's responsible for dragging
     * widgets.
     */
    private Mouse mouse;
    /**
     * Whether or not it is possible to move widgets within a subsystem.
     */
    private static boolean editable = false;
    /**
     * A widget being dragged.
     */
    private Widget selected = null;
    /**
     * A list of components within this subsystem.
     */
    private final ArrayList<Widget> widgets = new ArrayList<Widget>(20);
    /**
     * Responsible for reading data from a save file.
     */
    private static SmartDashboardXMLReader reader;

    public LWSubsystem() {
        super(true);//listen for sub tables
        MainPanel.getPanel("LiveWindow").addSubsystem(this);
    }

    /**
     * Initializes this subsystem.
     */
    public void init() {
        layout = new BoxLayout(this, BoxLayout.Y_AXIS);
        setLayout(layout);
        setObstruction(true);
        setOpaque(true);
        setVisible(true);
        setBorder(BorderFactory.createTitledBorder(getFieldName()));
        mouse = new Mouse(this);
        addMouseListener(mouse);
        addMouseMotionListener(mouse);
    }

    private void addSubsystemElement(String key, ITable value) {
        try {
            System.out.println("\nSubsystem \"" + getFieldName() + "\" does not contain widget \"" + key + "\"");
            System.out.println("Table: " + value);
            System.out.println("Type: " + value.getString("~TYPE~"));
            System.out.println("Trying to add a widget of type \"" + DataType.getType(value) + "\" and key " + key);
            Class<? extends Widget> widgetClass = DataType.getType(value).getDefault();
            Widget widget = widgetClass.newInstance();
            widget.setFieldName(key);
            widget.setType(DataType.getType(value));
            widget.init();
            widget.setValue(value);
            widgets.add(widget);
            add(widget);
            preferredSize = layout.preferredLayoutSize(this);
            setPreferredSize(preferredSize);
            setMinimumSize(preferredSize);
            setSavedSize(preferredSize);
            setSize(preferredSize);
            System.out.println("New size [" + preferredSize.width + ", " + preferredSize.height + "]");
            revalidate();
            repaint();
            System.out.println();
        } catch (InstantiationException ex) {
            Logger.getLogger(LWSubsystem.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(LWSubsystem.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @param source Required by ITableListener. Not used.
     * @param key The name of the changed table.
     * @param value The table that has been changed.
     * @param isNew Required by ITableListener. Not used.
     */
    @Override
    public void tableChanged(ITable source, final String key, final ITable table, boolean isNew) {
        boolean alreadyHasWidget = false;
        if (reader != null) {
            alreadyHasWidget = reader.containsWidgetOfName(this, key);
        }

        if (!alreadyHasWidget) {
            table.addTableListener("~TYPE~", new ITableListener() {
                public void valueChanged(final ITable typeSource, final String typeKey, final Object typeValue, final boolean typeIsNew) {
                    table.removeTableListener(this);
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            addSubsystemElement(key, table);
                        }
                    });
                }
            }, true);
        }
    }

    /**
     * Sets the reader responsible for loading a save file.
     *
     * @param XMLreader
     */
    public static void setLoaded(SmartDashboardXMLReader XMLreader) {
        reader = XMLreader;
    }

    @Override
    public void propertyChanged(Property property) {
    }

    /**
     * Gets a widget at a specific point. Used for dragging.
     */
    public Widget getWidgetAt(Point p) {
        if (getComponentAt(p) instanceof Widget) {
            Widget w = (Widget) getComponentAt(p);
            return w;
        } else {
            return null;
        }
    }

    /**
     * Gets all the widgets contained within this subsystem.
     */
    public ArrayList<Widget> getWidgets() {
        return widgets;
    }

    /**
     * Adds the specified widget to this subsystem.
     *
     * @param widget The widget to add.
     */
    public void addWidget(Widget widget) {
        widgets.add(widget);
    }

    /**
     * Sets whether or not it is possible to edit subsystems.
     */
    public static void setEditable(boolean editable) {
        LWSubsystem.editable = editable;
    }

    /**
     * @return whether or not it is possible to edit subsystems.
     */
    public static boolean isEditable() {
        return editable;
    }

    /**
     * Used for DnD for widgets within a subsystem.
     */
    private class Mouse extends MouseAdapter {

        int yclick = 0;
        private final LWSubsystem subsystem;

        public Mouse(LWSubsystem subsystem) {
            this.subsystem = subsystem;
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (editable) {
                if (selected == null) {
                    selected = getWidgetAt(e.getPoint());
                }
                if (selected != null) {

                    final int mouseY = e.getY(); // Mouse's y-coordinate. We don't care about its x-coord
                    int index = subsystem.getComponentZOrder(selected); // The index of the dragged widget
                    int newIndex = index + (mouseY < yclick ? -1 : 1); // The index to insert the dragged widget

                    boolean goingUp = false, // Flags for which direction the widget is being dragged
                            goingDown = false;

                    // Check to see if the new index is within acceptable bounds
                    if (newIndex >= 0 && newIndex < (subsystem.getComponentCount())) {
                        goingUp = newIndex < index;
                        goingDown = newIndex > index;
                    }

                    if (goingUp || goingDown) {
                        Component next = subsystem.getComponent(newIndex);

                        // If the stars align... move the widget
                        if ((goingDown && mouseY > next.getY() + next.getHeight())
                                || (goingUp && mouseY < next.getY())) {
                            subsystem.remove(selected);
                            subsystem.add(selected, newIndex);
                        }

                        subsystem.revalidate();
                        subsystem.repaint();
                    }
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            yclick = e.getY();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            selected = null;
            yclick = 0;
        }
    }
}
