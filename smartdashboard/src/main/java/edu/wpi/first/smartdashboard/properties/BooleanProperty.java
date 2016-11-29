package edu.wpi.first.smartdashboard.properties;

import java.awt.Component;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Joe Grinstead
 * @author Jeff Copeland
 */
public class BooleanProperty extends TextInputProperty<Boolean> {

    private JCheckBox box = new JCheckBox();
    private DefaultCellEditor checkbox = new DefaultCellEditor(box); 
    private TableCellRenderer renderer = new TableCellRenderer() {
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return box; 
        }
    }; 
    
    
    public BooleanProperty(PropertyHolder element, String name) {
        super(Boolean.class, element, name);
    }

    public BooleanProperty(PropertyHolder element, String name, boolean defaultValue) {
        super(Boolean.class, element, name, defaultValue);
    }

    @Override
    protected Boolean transformValue(Object value) {
        if (value instanceof String) {
            if ("true".equalsIgnoreCase((String) value)) {
                return true;
            } else if ("false".equalsIgnoreCase((String) value)) {
                return false;
            } else {
                return null;
            }
        } else {
            return super.transformValue(value);
        }
    }

    @Override
    public TableCellRenderer getRenderer() {
        box.setSelected(getValue());
        return renderer;  
    }

    @Override
    public TableCellEditor getEditor(Component c) {
        box.setSelected(getValue());
        return checkbox; 
    }
    
}
