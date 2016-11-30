package edu.wpi.first.smartdashboard.properties;

/**
 *
 * @author Joe Grinstead
 */
public class IntegerProperty extends NumberProperty {

    public IntegerProperty(PropertyHolder element, String name) {
        super(element, name);
    }

    public IntegerProperty(PropertyHolder element, String name, int defaultValue) {
        super(element, name, defaultValue);
    }

    @Override
    protected Integer transformValue(Object value) {
        Number number = super.transformValue(value);
        return number == null ? null : number.intValue();
    }

    @Override
    public Integer getValue() {
        return (Integer) super.getValue();
    }

    @Override
    public Number getDefault() {
        return (Integer) super.getDefault();
    }
}
