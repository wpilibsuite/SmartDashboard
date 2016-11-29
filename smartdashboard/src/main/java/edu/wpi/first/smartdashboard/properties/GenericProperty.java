package edu.wpi.first.smartdashboard.properties;

/**
 *
 * @author Joe Grinstead
 */
public abstract class GenericProperty<T> extends Property {

    private final Class<T> clazz;

    public GenericProperty(Class<T> clazz, PropertyHolder element, String name) {
        super(element, name);

        this.clazz = clazz;
    }

    public GenericProperty(Class<T> clazz, PropertyHolder element, String name, T defaultValue) {
        super(element, name, defaultValue);

        this.clazz = clazz;
    }

    @Override
    protected T transformValue(Object value) {
        return clazz.isInstance(value) ? clazz.cast(value) : null;
    }

    @Override
    public T getValue() {
        return clazz.cast(super.getValue());
    }

    @Override
    public T getDefault() {
        return clazz.cast(super.getDefault());
    }
}
