package edu.wpi.first.smartdashboard.gui;

import java.awt.*;
import java.lang.reflect.*;
import java.util.*;

import javax.swing.*;

import edu.wpi.first.smartdashboard.properties.*;

/**
 * This class is the super class of everything which can be seen on the SmartDashboard
 * besides the file menu.
 * @author pmalmsten
 * @author Joe Grinstead
 */
public abstract class DisplayElement extends JPanel implements PropertyHolder {

    /** The point that the element was saved at or should save to */
    private Point savedLocation = new Point(0, 0);
    /** The saved dimension, -1 means the preferred dimension should be used */
    private Dimension savedDimension = new Dimension(-1, -1);
    /** The property map */
    private Map<String, Property> properties = new LinkedHashMap<String, Property>();
    /** Whether or not this element can be resized by the user dragging on an edge */
    private boolean resizable = true;
    /** Whether or not new display elements should not be placed on this object */
    private boolean obstruction = true;

    /**
     * Instantiates the element.  Most creation code should be done in the
     * {@link DisplayElement#init() init()} method.
     */
    public DisplayElement() {
        setOpaque(false);
    }

    /**
     * Sets up and displays any internal subcomponents managed by this UI element.
     *
     * This will be called from within the GUI thread, so don't worry about
     * running things in the EventQueue. A DisplayElement should be fully drawn
     * on the screen and ready to receive data when this method returns.
     */
    public abstract void init();

    /**
     * This method is called when an element is removed, and should be used to
     * clean up any loose ends.  It will be called from the GUI thread.
     */
    public void disconnect() {
    }

    public boolean validatePropertyChange(Property property, Object value) {
        return true;
    }

    /**
     * Returns whether or not this element should be considered an "obstruction."
     *
     * <p>An obstruction is any object which the SmartDashboard should automatically
     * try to avoid when placing new {@link DisplayElement DisplayElements}.
     * Pretty much everything is an obstruction except for {@link edu.wpi.first.smartdashboard.gui.elements.Image Images}
     * because images will sometimes be used for background effect, in which case it is
     * fine if a new {@link DisplayElement} spawns over it.</p>
     * @return whether or not this element should be considered an obstruction
     */
    public boolean isObstruction() {
        return obstruction;
    }

    /**
     * Sets whether or not this element is an "obstruction."
     *
     * <p>An obstruction is any object which the SmartDashboard should automatically
     * try to avoid when placing new {@link DisplayElement DisplayElements}.
     * Pretty much everything is an obstruction except for {@link edu.wpi.first.smartdashboard.gui.elements.Image Images}
     * because images will sometimes be used for background effect, in which case it is
     * fine if a new {@link DisplayElement} spawns over it.</p>
     * @param obstruction whether or not it should be an obstruction
     */
    public void setObstruction(boolean obstruction) {
        this.obstruction = obstruction;
    }

    public Map<String, Property> getProperties() {
        return properties;
    }

    /**
     * Returns the point that the element was saved at or should save to.
     * @return the point that the element was saved at or should save to
     */
    public Point getSavedLocation() {
        return new Point(savedLocation);
    }

    /**
     * Returns the saved size of this element.
     * @return the saved size of this element
     */
    public Dimension getSavedSize() {
        return new Dimension(savedDimension);
    }

    /**
     * Sets the saved location for this element.
     * @param p the location that this element should save to a file
     */
    public void setSavedLocation(Point p) {
        savedLocation = p;
    }

    /**
     * Sets the saved dimension for this element.  If a value is -1,
     * the preferred size will be used internally.
     * @param d the dimension
     */
    public void setSavedSize(Dimension d) {
        savedDimension = new Dimension(d);
    }

    /**
     * Returns whether or not this element can be resized by the user dragging on an edge.
     * @return whether or not this element can be resized by the user dragging on an edge
     */
    public boolean isResizable() {
        return resizable;
    }

    /**
     * Sets whether or not this element can be resized by the user dragging on an edge.
     */
    public void setResizable(boolean resizable) {
        this.resizable = resizable;
    }

    protected void update(Property property, Object defaultValue) {
        if (property.hasDefault()) {
            propertyChanged(property);
        } else if (property.hasValue()) {
            property.setDefault(defaultValue);
            propertyChanged(property);
        } else {
            property.setDefault(defaultValue);
        }
    }

    public static String getName(Class<? extends DisplayElement> clazz) {
        try {
            Field field = clazz.getDeclaredField("NAME");
            int modifiers = field.getModifiers();
            if (!Modifier.isStatic(modifiers)) {
                throw new RuntimeException("TYPES must be static");
            } else if (!Modifier.isFinal(modifiers)) {
                throw new RuntimeException("TYPES must be final");
            }
            if (Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)) {
                return (String) field.get(null);
            }
        } catch (Exception e) {
        }

        return clazz.getSimpleName();
    }
}
