package edu.wpi.first.smartdashboard.properties;

import java.awt.*;
import java.util.*;

import javax.swing.*;
import javax.swing.table.*;

/**
 *
 * @author Jeff Copeland
 */
public class MultiProperty extends Property {
   public MultiProperty(PropertyHolder holder, String name) {
      super(holder, name);
   }

    private JComboBox comboBox = new JComboBox();
    private DefaultCellEditor cellEditor = new DefaultCellEditor(comboBox);
    private Map<String,Object> values = new HashMap<String, Object>();
    private TableCellRenderer renderer = new TableCellRenderer() {
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return comboBox;
        }
    };

    public void add(String key, Object value) {
        comboBox.addItem(key);
        values.put(key, value);
    }
    
    @Override
    public boolean setDefault(Object key) {
        if (super.setDefault(key)) {
            if (!hasValue()) {
                comboBox.setSelectedItem(key);
            }
            return true;
        }
        return false; 
    }

    @Override
    protected void valueChanged() {
        comboBox.setSelectedItem(getSaveValue());
    }

    @Override
    public TableCellEditor getEditor(Component c) {
        return cellEditor; 
    }

    @Override
    protected Object transformValue(Object value) {
        return values.get(value);
    }

    @Override
    public TableCellRenderer getRenderer() {
        return renderer;
    }

    @Override
    public String getSaveValue() {
       
        for(Map.Entry<String,Object> entry: values.entrySet()) {
            if (entry.getValue().equals(getValue())) {
                return entry.getKey(); 
            }
        }
        return null; 
    }
}