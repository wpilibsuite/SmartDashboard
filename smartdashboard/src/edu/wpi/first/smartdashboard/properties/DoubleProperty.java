package edu.wpi.first.smartdashboard.properties;

/**
 *
 * @author Joe Grinstead
 */
public class DoubleProperty extends NumberProperty {

    public DoubleProperty(PropertyHolder element, String name) {
        super(element, name);
    }

    public DoubleProperty(PropertyHolder element, String name, double defaultValue) {
        super(element, name, defaultValue);
    }

    @Override
    protected Double transformValue(Object value) {
        Number number = super.transformValue(value);
        return number == null ? null : number.doubleValue();
    }

    @Override
    public Double getValue() {
        return (Double) super.getValue();
    }

    @Override
    public Double getDefault() {
        return (Double) super.getDefault();
    }
}
