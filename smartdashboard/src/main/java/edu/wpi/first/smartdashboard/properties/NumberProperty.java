package edu.wpi.first.smartdashboard.properties;

/**
 *
 * @author Joe Grinstead
 */
public class NumberProperty extends TextInputProperty<Number> {

    public NumberProperty(PropertyHolder element, String name) {
        super(Number.class, element, name);
    }

    public NumberProperty(PropertyHolder element, String name, Number defaultValue) {
        super(Number.class, element, name, defaultValue);
    }

    @Override
    protected Number transformValue(Object value) {
        if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                try {
                    return Double.parseDouble((String) value);
                } catch (NumberFormatException ex) {
                    return null;
                }
            }
        } else {
            return super.transformValue(value);
        }
    }
}
