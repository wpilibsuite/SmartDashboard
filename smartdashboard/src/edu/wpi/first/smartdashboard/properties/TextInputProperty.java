package edu.wpi.first.smartdashboard.properties;

import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Joe Grinstead
 */
public abstract class TextInputProperty<T> extends GenericProperty<T> {

    protected TextInputProperty(Class<T> clazz, PropertyHolder element, String name) {
        super(clazz, element, name);
    }

    protected TextInputProperty(Class<T> clazz, PropertyHolder element, String name, T defaultValue) {
        super(clazz, element, name, defaultValue);
    }

    @Override
    public TableCellRenderer getRenderer() {
        return null;
    }
}
