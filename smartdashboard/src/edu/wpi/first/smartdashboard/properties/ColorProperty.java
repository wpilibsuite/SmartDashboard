package edu.wpi.first.smartdashboard.properties;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.EventObject;
import javax.swing.AbstractCellEditor;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Joe Grinstead
 */
public class ColorProperty extends GenericProperty<Color> {

    public ColorProperty(PropertyHolder parent, String name) {
        super(Color.class, parent, name);
    }

    public ColorProperty(PropertyHolder parent, String name, Color defaultValue) {
        super(Color.class, parent, name, defaultValue);
    }

    @Override
    protected Color transformValue(Object value) {
        if (value instanceof String) {
            try {
                String string = (String) value;
                int index = string.indexOf('.');
                int red = Integer.parseInt(string.substring(0, index));
                string = string.substring(index + 1);
                index = string.indexOf('.');
                int green = Integer.parseInt(string.substring(0, index));
                string = string.substring(index + 1);
                index = string.indexOf('.');
                int blue = Integer.parseInt(string.substring(0, index));
                int alpha = Integer.parseInt(string.substring(index + 1));
                return new Color(red, green, blue, alpha);
            } catch (Exception e) {
                return null;
            }
        } else {
            return super.transformValue(value);
        }
    }

    @Override
    public String getSaveValue() {
        Color color = (Color) getValue();
        return color.getRed() + "." + color.getGreen() + "." + color.getBlue() + "." + color.getAlpha();
    }

    @Override
    public TableCellEditor getEditor(Component c) {
        return new ColorTableCellEditor(c);
    }
    private ColorTableCellRenderer renderer;

    @Override
    public TableCellRenderer getRenderer() {
        if (renderer == null) {
            renderer = new ColorTableCellRenderer();
        }
        return renderer;
    }

    class ColorTableCellEditor extends AbstractCellEditor implements TableCellEditor {

        private JColorChooser colorChooser;
        private JDialog colorDialog;
        private JPanel panel;

        public ColorTableCellEditor(Component c) {
            panel = new JPanel();
            colorChooser = new JColorChooser();
            colorDialog = JColorChooser.createDialog(c, "Color editor", true, colorChooser,
                    new ActionListener() {  // OK Button listener

                        public void actionPerformed(ActionEvent ev) {
                            stopCellEditing();
                        }
                    }, new ActionListener() { // Cancel button listener

                public void actionPerformed(ActionEvent ev) {
                    cancelCellEditing();
                }
            });
            colorDialog.addWindowListener(new WindowAdapter() {

                @Override
                public void windowClosing(WindowEvent event) {
                    cancelCellEditing();
                }
            });
        }

        @Override
        public boolean shouldSelectCell(EventObject av) {
            // start editing and tell caller it's OK to edit this cell
            colorDialog.setVisible(true);
            return true;
        }

        @Override
        public void cancelCellEditing() {
            // editing is calceled -- hide dialog
            colorDialog.setVisible(false);
            super.cancelCellEditing();
        }

        @Override
        public boolean stopCellEditing() {
            // editing is complete -- hide dialog
            colorDialog.setVisible(false);
            super.stopCellEditing();
            return true;
        }

        public Object getCellEditorValue() {
            return colorChooser.getColor();
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int col) {
            // get the current color and store in the dialog in case the user starts
            // editing it
            colorChooser.setColor((Color) value);
            return panel;
        }
    }

    class ColorTableCellRenderer extends JPanel implements TableCellRenderer {

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            setBackground((Color) value);
            if (hasFocus) {
                setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
            } else {
                setBorder(null);
            }
            return this;
        }
    }
}
