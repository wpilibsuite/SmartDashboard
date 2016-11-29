package edu.wpi.first.smartdashboard.properties;

import java.awt.Component;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/**
 *
 *
 * @author Joe Grinstead
 */
public abstract class Property {

    private final PropertyHolder element;
    private final String name;
    private Object defaultValue;
    private Object value;

    protected Property(PropertyHolder element, String name) {
        this.element = element;
        this.name = name;

        element.getProperties().put(name, this);
    }

    protected Property(PropertyHolder element, String name, Object defaultValue) {
        this.element = element;
        this.name = name;
        this.defaultValue = defaultValue;

        element.getProperties().put(name, this);
    }

    public String getName() {
        return name;
    }

    public boolean hasDefault() {
        return defaultValue != null;
    }

    public boolean setDefault(Object value) {
        value = transformValue(value);
        if (value != null) {
            defaultValue = value;
            return true;
        } else {
            return false;
        }
    }

    public boolean setValue(Object value) {
        value = transformValue(value);
        if (value != null && element.validatePropertyChange(this, value)) {
            this.value = value;
            valueChanged();
            element.propertyChanged(this);
            return true;
        } else {
            return false;
        }
    }

    public Object getValue() {
        return value == null ? defaultValue : value;
    }

    public Object getDefault() {
        return defaultValue;
    }

    protected abstract Object transformValue(Object value);

    public String getSaveValue() {
        return getValue().toString();
    }

    public void setSaveValue(String value) {
        _setValue(value);
    }

    protected void _setValue(Object value) {
        value = transformValue(value);
        if (value != null) {
            this.value = value;
            valueChanged();
        }
    }

    protected void valueChanged() {
    }

    public boolean isDefault() {
        return value == null ? defaultValue != null : value.equals(defaultValue);
    }

    public boolean hasValue() {
        return value != null;
    }

    public Object getTableValue() {
        return getValue();
    }

    public abstract TableCellRenderer getRenderer();

    public TableCellEditor getEditor(Component c) {
        return null;
    }
}
