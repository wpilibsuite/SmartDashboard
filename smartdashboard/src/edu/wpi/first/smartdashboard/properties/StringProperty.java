package edu.wpi.first.smartdashboard.properties;

/**
 *
 * @author Joe Grinstead
 */
public class StringProperty extends TextInputProperty<String> {

    public StringProperty(PropertyHolder element, String name) {
        super(String.class, element, name);
    }

    public StringProperty(PropertyHolder element, String name, String defaultValue) {
        super(String.class, element, name, defaultValue);
    }

    @Override
    protected String transformValue(Object value) {
        return value.toString();
    }
}
