package edu.wpi.first.smartdashboard.properties;

import java.awt.*;
import java.io.*;

import javax.swing.*;
import javax.swing.table.*;

import org.jfree.ui.*;

/**
 *
 * @author Joe Grinstead
 */
public class FileProperty extends GenericProperty<String> {

    protected JFileChooser chooser;

    public FileProperty(PropertyHolder parent, String name) {
        super(String.class, parent, name);

        chooser = new JFileChooser();
    }

    public FileProperty(PropertyHolder parent, String name, String defaultValue) {
        super(String.class, parent, name, defaultValue);

        chooser = new JFileChooser(getValue());
    }

    @Override
    protected String transformValue(Object value) {
        if (value instanceof String) {
            return (String) value;
        } else if (value instanceof File) {
            return ((File) value).getPath();
        } else {
            return null;
        }
    }

    public void addExtensionFilter(String description, String extension){
        chooser.addChoosableFileFilter(new ExtensionFileFilter(description, extension.startsWith(".")?extension:"."+extension));
    }

    @Override
    public TableCellRenderer getRenderer() {
        return null;
    }

    @Override
    public TableCellEditor getEditor(Component c) {
        return new FileTableCellEditor(c);
    }

    private class FileTableCellEditor extends AbstractCellEditor implements TableCellEditor {

        private Component c;
        private JLabel label;

        public FileTableCellEditor(Component c) {
            this.c = c;
            label = new JLabel();
        }

        public Object getCellEditorValue() {
            return getValue();
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            chooser.setSelectedFile(new File(getValue()));
            switch (chooser.showDialog(c, "Select")) {
                case JFileChooser.APPROVE_OPTION:
                    setValue(chooser.getSelectedFile());
                    break;
                case JFileChooser.ERROR_OPTION:
                case JFileChooser.CANCEL_OPTION:
                default:
            }
            label.setText(getValue().toString());
            return label;
        }
    }
}
