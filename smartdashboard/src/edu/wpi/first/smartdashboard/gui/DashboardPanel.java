package edu.wpi.first.smartdashboard.gui;

import edu.wpi.first.smartdashboard.livewindow.elements.LWSubsystem;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

import edu.wpi.first.smartdashboard.types.*;
import edu.wpi.first.wpilibj.tables.*;

/**
 * This is the main panel, it sits within the {@link DashboardFrame} and
 * contains everything the user sees except for the window outline. This class
 * is the workhorse of the GUI. Inside here is where the logic is contained for
 * how to respond to new fields and various other things.
 *
 * @author Joe Grinstead
 */
public class DashboardPanel extends JPanel {

    /**
     * We use a glass pane technique for editable mode
     */
    private GlassPane glassPane;
    /**
     * This panel contains everything except the glass pane
     */
    private JPanel backPane = new JPanel();
    /**
     * All the elements currently being displayed
     */
    private LinkedList<DisplayElement> elements = new LinkedList<DisplayElement>();
    /**
     * All the fields that are currently being displayed
     */
    private Map<String, Widget> fields = new HashMap<String, Widget>();
    /**
     * All the fields which are hidden (they have no widget)
     */
    private Set<String> hiddenFields = new HashSet<String>();
    /**
     * Whether or not this is editable
     */
    private boolean editable = false;
    /**
     * The listener which connects the panel to keep up to date on the robot
     */
    private final RobotListener listener = new RobotListener();
    private final ArrayList<LWSubsystem> subsystems = new ArrayList<LWSubsystem>();
    private final DashboardFrame frame;
    private final ITable table;

    /**
     * Instantiates the panel
     */
    public DashboardPanel(DashboardFrame frame, ITable table) {
        this.frame = frame;
        this.table = table;
        glassPane = new GlassPane(frame, this);
        add(glassPane);
        add(backPane);

        backPane.setLayout(new DashboardLayout());
        backPane.setFocusable(true);

        setLayout(new DashboardLayout());

        setEditable(editable);

        table.addTableListener(listener, true);
        table.addSubTableListener(listener);
    }

    public ITable getTable() {
        return table;
    }

    @Override
    public synchronized void addMouseListener(MouseListener l) {
        glassPane.addMouseListener(l);
        backPane.addMouseListener(l);
    }

    @Override
    public synchronized void addMouseMotionListener(MouseMotionListener l) {
        glassPane.addMouseMotionListener(l);
        backPane.addMouseMotionListener(l);
    }

    /**
     * Revalidates the content behind the glass pane
     */
    public void revalidateBacking() {
        backPane.revalidate();
    }

    /**
     * Sets whether or not this panel is editable. If the panel becomes
     * editable, then it will pull the focus from the widgets.
     *
     * @param editable whether or not the pane should be editable
     */
    public void setEditable(boolean editable) {
        this.editable = editable;

        glassPane.setVisible(editable);
        if (editable) {
            glassPane.requestFocus();
        }
    }

    /**
     * Returns whether or not this panel is editable.
     *
     * @return whether or not this panel is editable
     */
    public boolean isEditable() {
        return editable;
    }

    /**
     * Returns the names of all the fields marked as hidden (they are the ones
     * in the {@link ITable} that have been explicitly declared to be ignored by
     * the user).
     *
     * @return the hidden fields
     */
    public Iterable<String> getHiddenFields() {
        return hiddenFields;
    }

    /**
     * Returns all the {@link DisplayElement DisplayElements} that are in this
     * panel.
     *
     * @return all the elements
     */
    public Iterable<DisplayElement> getElements() {
        return elements;
    }

    /**
     * Takes the given value and manipulates it based on the given type to be
     * able to be handed to a {@link Widget}. Basically, if the type is a
     * primitive, the value will be returned. If the type is a
     * {@link NamedDataType}, then the data subtable will be returned. If either
     * the type of the value is {@code null}, then {@code null} will be
     * returned.
     *
     * @param type the type of the data
     * @param value the data
     * @return the data to be handed to a {@link Widget}
     */
    private Object verifyValue(DataType type, Object value) {
        if (type == null || value == null) {
            return null;
        } else if (type instanceof NamedDataType) {
            return value;
        } else {
            return value;
        }
    }

    /**
     * Clears the panel of all of its data, and then reload it. This basically
     * starts the panel over from the beginning.
     */
    public void clear() {
        hiddenFields.clear();
        fields.clear();
        for (DisplayElement element : elements) {
            disconnect(element);
            backPane.remove(element);
        }
        elements.clear();



        table.removeTableListener(listener);
        table.addTableListener(listener, true);
        table.addSubTableListener(listener);

        repaint();
    }

    /**
     * Removes all fields which do not have values in the
     */
    public void removeUnusedFields() {
        ArrayList<String> unused = new ArrayList<String>();
        for (String field : fields.keySet()) {
            if (!(table.containsKey(field) || table.containsSubTable(field))) {
                unused.add(field);
            }
        }
        for (String field : unused) {
            removeField(field);
            hiddenFields.remove(field);
        }
    }

    /**
     * Removes the field with the given name from the screen. The field will
     * then be added to the list of fields which should remain hidden.
     *
     * @param field the field to remove
     */
    public void removeField(String field) {
        Widget elem = fields.get(field);
        hiddenFields.add(field);
        if (elem != null) {
            disconnect(elem);
            backPane.remove(elem);
            fields.remove(field);
            elements.remove(elem);
            repaint(elem.getBounds());
        }
    }

    /**
     * Removes the given element from the screen. If you would like to remove a
     * {@link Widget} instead of a {@link StaticWidget}, then consider using
     * {@link DashboardPanel#removeField(java.lang.String) removeField(...)}
     *
     * @param widget the widget to remove
     */
    public void removeElement(StaticWidget widget) {
        disconnect(widget);
        backPane.remove(widget);
        elements.remove(widget);
        repaint(widget.getBounds());
    }

    /**
     * Shifts the given element behind all the other ones. Every other element
     * will then be drawn in front of the given one.
     *
     * @param element the element to shift
     */
    public void shiftToBack(DisplayElement element) {
        int count = 0;

        elements.remove(element);

        for (DisplayElement e : elements) {
            backPane.setComponentZOrder(e, count++);
        }
        backPane.setComponentZOrder(element, count);

        elements.add(element);

        repaint();
    }

    /**
     * Adds the given display element to the screen, putting it at the given
     * point. If the given point is {@code null}, then it will find a place to
     * put it. If the point is not {@code null}, then the given
     * {@link DisplayElement} should already have set its size.
     *
     * @param element the element to add
     * @param point the location to put it (or null, if one needs to be found)
     */
    public void addElement(DisplayElement element, Point point) {
        // Initialize the element
        element.init();

        // Set the elements location
        if (point == null) {
            Dimension saved = element.getSavedSize();
            Dimension preferred = element.getPreferredSize();
            if (saved.width > 0) {
                preferred.width = saved.width;
            }
            if (saved.height > 0) {
                preferred.height = saved.height;
            }
            element.setSize(preferred);
            point = findSpace(element);
            element.setBounds(new Rectangle(point, preferred));
        }
        element.setSavedLocation(point);

        backPane.add(element);

        // Put the new element in front (shift everything back first)
        int count = 1;
        for (DisplayElement e : elements) {
            backPane.setComponentZOrder(e, count++);
        }
        backPane.setComponentZOrder(element, 0);

        elements.addFirst(element);

        // Repaint
        revalidate();
        repaint();
    }

    /**
     * Sets the element to use for the given field, removing the current element
     * if one exists for that field. This is used mostly by the
     * {@link DashboardFrame#load(java.lang.String) load(...)} method.
     *
     * @param key the name of the field
     * @param element the element to give to that field
     * @param type the type of the data
     * @param value the value of the data
     * @param point the point to put it
     */
    public void setField(String key, Widget element, DataType type, Object value, Point point) {
        removeField(key);

        hiddenFields.remove(key);

        value = verifyValue(type, value);

        element.setFieldName(key);
        if (type != null) {
            element.setType(type);
        }

        fields.put(key, element);

        addElement(element, point);

        if (value != null) {
            element.setValue(value);
        }
    }

    /**
     * Adds the field of the given name to the screen. The field does not need
     * to be in the SmartDashboard table. This will add some default widget (for
     * that type) with no value.
     *
     * @param key the key to add
     */
    public void addField(String key) {
        setField(key, null, table.containsKey(key) ? table.getValue(key) : null, null);
    }

    /**
     * Sets the field to use the given values.
     *
     * @param key the key of the field
     * @param preferred the preferred widget to use
     * @param value the value of the field (must <b>not</b> be {@code null})
     * @param point the point the widget should be (can be {@code null})
     */
    public void setField(String key, Class<? extends Widget> preferred, Object value, Point point) {
        setField(key, preferred, DataType.getType(value), value, point);
    }

    /**
     * Sets the field to use the given values.
     *
     * @param key the key of the field
     * @param preferred the preferred widget to use
     * @param type the type of the field
     * @param value the value of the field (can be {@code null})
     * @param point the point the widget should be (can be {@code null})
     */
    @SuppressWarnings("unchecked")
    public void setField(String key, Class<? extends Widget> preferred, final DataType type, Object value, Point point) {
        Widget element = fields.get(key);

        if (type == null) {
            System.out.println("WARNING: has no way of handling data at field \"" + key + "\"");
            removeField(key);
        } else if (element != null && preferred == null && (element.getType() == type || element.supportsType(type))) {
            if (element.getType() != type) {
                element.setType(type);
            }

            value = verifyValue(type, value);
            if (value != null) {
                element.setValue(value);
            }
        } else {
            Class<? extends Widget> clazz = preferred == null ? type.getDefault() : preferred;

            if (clazz == null) {
                Set<Class<? extends Widget>> candidates = DisplayElementRegistry.getWidgetsForType(type);

                if (candidates.isEmpty()) {
                    System.out.println("WARNING: has no way of handling type " + type);
                    return;
                } else {
                    clazz = (Class<? extends Widget>) candidates.toArray()[0];
                }
            }

            try {
                element = clazz.newInstance();

                setField(key, element, type, value, point);
            } catch (InstantiationException ex) {
                System.out.println("ERROR: " + clazz.getName() + " has no default constructor!");
            } catch (IllegalAccessException ex) {
                System.out.println("ERROR: " + clazz.getName() + " has no public default constructor!");
            }
        }
    }

    /**
     * Returns the element that covers the given point. It will return the
     * forward most element.
     *
     * @param point the point on the screen
     * @return the element
     */
    public DisplayElement findElementContaining(Point point) {
        for (DisplayElement element : elements) {
            if (element.getBounds().contains(point)) {
                return element;
            }
        }
        return null;
    }
    /**
     * Just a standard random
     */
    private static final Random random = new Random();

    /**
     * Finds a space to put the newest element, using its preferred size
     *
     * @param toPlace the element to place
     * @return the place where it should go
     */
    private Point findSpace(DisplayElement toPlace) {
        Stack<Point> positions = new Stack<Point>();
        positions.add(new Point(0, 0));

        Dimension size = toPlace.getSize();
        Dimension panelBounds = getSize();

        PositionLoop:
        while (!positions.isEmpty()) {
            Point position = positions.pop();
            Rectangle area = new Rectangle(position, size);

            if (area.x < 0 || area.y < 0
                    || area.x + area.width > panelBounds.width
                    || area.y + area.height > panelBounds.height) {
                continue;
            }

            for (DisplayElement element : elements) {
                if (element != toPlace && element.isObstruction()) {
                    Rectangle bounds = element.getBounds();
                    // Test Intersection
                    if (!(bounds.x > area.x + area.width
                            || bounds.x + bounds.width < area.x
                            || bounds.y > area.y + area.height
                            || bounds.y + bounds.height < area.y)) {
                        Point right = new Point(bounds.x + bounds.width + 1, position.y);
                        if (positions.isEmpty()) {
                            positions.add(right);
                            right = null;
                        }
                        positions.add(new Point(position.x, bounds.y + bounds.height + 1));
                        if (right != null && Math.abs(right.x - area.x) < area.width / 3) {
                            positions.add(right);
                        }
                        continue PositionLoop;
                    }
                }
            }

            System.out.println("Adding an element at [" + position.x + "," + position.y + "]");
            return position;
        }

        // If no space was found, jumble them at the beginning
        return new Point(random.nextInt(32), random.nextInt(32));
    }

    /**
     * Calls the {@link DisplayElement#disconnect() disconnect()} method of the
     * given {@link DisplayElement}, catching and printing any exceptions in the
     * process.
     *
     * @param element the element to disconnect
     */
    private void disconnect(DisplayElement element) {
        try {
            element.disconnect();
        } catch (Exception e) {
            String message = "An exception occurred while removing the "
                    + DisplayElement.getName(element.getClass())
                    + " of type " + e.getClass()
                    + ".\nThe message is:\n" + e.getMessage()
                    + "\nThe stack trace is:\n";
            for (StackTraceElement trace : e.getStackTrace()) {
                message += trace.toString() + "\n";
            }
            JOptionPane.showMessageDialog(frame,
                    message,
                    "Exception When Removing Element", JOptionPane.ERROR_MESSAGE);
        }
    }

    private class RobotListener implements ITableListener {

        @Override
        public void valueChanged(final ITable source, final String key, final Object value, final boolean isNew) {
            if (isNew && !frame.getPrefs().autoShowWidgets.getValue() && !fields.containsKey(key)) {
                hiddenFields.add(key);
            } else {
                if (!hiddenFields.contains(key)) {
                    if (value instanceof ITable) {
                        final ITable table = (ITable) value;
                        table.addTableListener("~TYPE~", new ITableListener() {
                            public void valueChanged(final ITable typeSource, final String typeKey, final Object typeValue, final boolean typeIsNew) {
                                table.removeTableListener(this);
                                SwingUtilities.invokeLater(new Runnable() {
                                    public void run() {
                                        setField(key, null, value, null);
                                    }
                                });
                            }
                        }, true);
                    } else {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                setField(key, null, value, null);
                            }
                        });
                    }
                }
            }

        }
    }

    /**
     * Gets the subsystems stored in this panel.
     */
    public ArrayList<LWSubsystem> getSubsystems() {
        return subsystems;
    }

    /**
     * For resetting the LiveWindow.
     *
     * @param subsystem The subsystem to add to this panel's list of subsystems.
     */
    public void addSubsystem(LWSubsystem subsystem) {
        subsystems.add(subsystem);
    }

    private class DashboardLayout implements LayoutManager {

        public void addLayoutComponent(String name, Component comp) {
        }

        public void removeLayoutComponent(Component comp) {
        }

        public Dimension preferredLayoutSize(Container parent) {
            return new Dimension(640, 480); // Not going to be used
        }

        public Dimension minimumLayoutSize(Container parent) {
            return new Dimension(0, 0); // Not going to be used
        }

        public void layoutContainer(Container parent) {
            if (parent == DashboardPanel.this) {
                Dimension size = getSize();
                glassPane.setBounds(0, 0, size.width, size.height);
                backPane.setBounds(0, 0, size.width, size.height);
            } else { // Back Pane
                for (DisplayElement element : elements) {
                    element.setLocation(element.getSavedLocation());

                    Dimension savedSize = element.getSavedSize();
                    Dimension preferredSize = element.getPreferredSize();
                    Dimension size = new Dimension(preferredSize);
                    if (savedSize != null && savedSize.width != -1) {
                        size.width = savedSize.width;
                    }
                    if (savedSize != null && savedSize.height != -1) {
                        size.height = savedSize.height;
                    }

                    element.setSize(size);
                }
            }
        }
    }
}
