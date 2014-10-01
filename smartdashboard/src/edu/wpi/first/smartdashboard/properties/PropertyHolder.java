/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.wpi.first.smartdashboard.properties;

import java.util.Map;

/**
 * This interface defines methods that allow an object to use the standard
 * property mechanism used in the SmartDashboard project.
 *
 * The system is designed to make the necessary coding for an extension developer
 * quite small.
 * @author Joe Grinstead
 * @see Property
 */
public interface PropertyHolder {

    /**
     * Returns a mapping between every property name and the corresponding property.
     * The map will be edited externally; it should not be a copy of the internal map.
     * @return a map
     */
    public Map<String, Property> getProperties();

    /**
     * This method allows the {@link PropertyHolder} to veto whether or not the
     * given {@link Property} should change to the given value.
     *
     * It is called whenever {@link Property#setValue(java.lang.Object) setValue(...)}
     * is called.
     *
     * <p>It is called only when the value if the value is acceptable.
     * In other words, it must not be {@code null} and it must correspond to
     * the type of element that the {@link Property} accepts.  So if the given
     * {@link Property} is an {@link IntegerProperty IntegerProperty}, then {@code value} will
     * be a non-null {@link Integer Integer} object.</p>
     *
     * <p>Note that this method will not be called when a property receives its value
     * from a save file</p>
     * @param property the {@link Property}
     * @param value the value to change it to
     * @return whether or not the change should be allowed
     */
    public boolean validatePropertyChange(Property property, Object value);

    /**
     * This method will be called after the given {@link Property Property's} value
     * has changed.
     * @param property the {@link Property} that changed
     */
    public void propertyChanged(Property property);

}
