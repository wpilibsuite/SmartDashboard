package edu.wpi.first.smartdashboard.gui.elements;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.table.*;

import edu.wpi.first.smartdashboard.gui.*;
import edu.wpi.first.smartdashboard.properties.*;
import edu.wpi.first.smartdashboard.robot.Robot;
import edu.wpi.first.wpilibj.tables.*;

/**
 *
 * @author Joe Grinstead
 */
public class RobotPreferences extends StaticWidget implements ITableListener {

    public static final String DELETED_VALUE = "\"";
    public static final String NAME = "Robot Preferences";
    private JTable table;
    private PreferenceTableModel model;
    private Map<String, String> values;
    private JButton save;
    private JButton add;
    private JButton remove;

    @Override
    public void init() {
        add = new JButton("Add");
        add.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                NewPreferenceEntryDialog dialog = new NewPreferenceEntryDialog();
                dialog.show(remove.getLocationOnScreen());
                if (!dialog.isCanceled()) {
                    model.put(dialog.getKey(), dialog.getValue());
                }
            }
        });

        remove = new JButton("Remove");
        remove.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (table.isEditing()) {
                    table.getCellEditor().cancelCellEditing();
                }
                Map.Entry<String, String> entry = model.getRow(table.getSelectedRow());
                if (entry != null) {
                    model.delete(entry.getKey());
                }
            }
        });

        save = new JButton("Save");
        save.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Robot.getPreferences().putBoolean(Robot.PREF_SAVE_FIELD, true);
            }
        });

        values = new LinkedHashMap<String, String>();

        Robot.getPreferences().addTableListener(this, true);

        model = new PreferenceTableModel();

        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);


        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(0, 2));
        buttonPanel.add(add);
        buttonPanel.add(remove);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BorderLayout());
        controlPanel.add(buttonPanel, BorderLayout.NORTH);
        controlPanel.add(save, BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        JScrollPane tableScrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(tableScrollPane, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
        
        setPreferredSize(new Dimension(300, 200));
    }

    @Override
    public void disconnect() {
        Robot.getPreferences().removeTableListener(this);
    }

    @Override
    public void propertyChanged(Property property) {
    }

	@Override
	public void valueChanged(ITable source, String key, Object value, boolean isNew) {
        if (key.equals(Robot.PREF_SAVE_FIELD)) {
            save.setEnabled(!(Boolean) value);
        } else {
            if (DELETED_VALUE.equals(value.toString())) {
                values.remove(key);
            } else {
                values.put(key, value.toString());
            }
        }

        if (model != null) {
            model.fireTableDataChanged();
        }
    }


    private class PreferenceTableModel extends AbstractTableModel {

        public int getRowCount() {
            return values.size();
        }

        public int getColumnCount() {
            return 2;
        }

        @Override
        public String getColumnName(int i) {
            if (i == 0) {
                return "Key";
            } else if (i == 1) {
                return "Value";
            } else {
                return "ERROR";
            }
        }

        public Map.Entry<String, String> getRow(int rowIndex) {
            int row = 0;
            for (Map.Entry<String, String> entry : values.entrySet()) {
                if (row++ == rowIndex) {
                    return entry;
                }
            }
            return null;
        }

        public boolean put(String key, String value) {
            if (validateKey(key) && validateValue(value)) {
                Robot.getPreferences().putString(key, value);
                return true;
            }
            return false;
        }

        public void delete(String key) {
            Robot.getPreferences().putString(key, DELETED_VALUE);
        }

        public boolean validateKey(String key) {
            if (key.isEmpty()) {
                JOptionPane.showMessageDialog(RobotPreferences.this, "The key cannot be empty", "Bad Key", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            if (key.contains(" ") || key.contains("=") || key.contains("\t") || key.contains("\r") || key.contains("\n")) {
                JOptionPane.showMessageDialog(RobotPreferences.this, "The key cannot containt ' ', '=', tabs or newlines", "Bad Key", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            return true;
        }

        public boolean validateValue(String value) {
            if (value.contains("\"")) {
                JOptionPane.showMessageDialog(RobotPreferences.this, "The value cannot contain '\"'", "Bad Value", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            return true;
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            if (columnIndex == 0) {//Key
                Map.Entry<String, String> entry = getRow(rowIndex);
                if (entry != null) {
                    String oldName = entry.getKey();
                    String value = entry.getValue();
                    if (!oldName.equals(aValue.toString())) {
                        if (!values.containsKey(aValue.toString())) {
                            if(put(aValue.toString(), value))
                                delete(oldName);
                        } else {
                            JOptionPane.showMessageDialog(RobotPreferences.this, "An entry with the key " + aValue + " already exists", "Duplicate Key", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            } else {//Value
                Map.Entry<String, String> entry = getRow(rowIndex);
                if (entry != null) {
                    put(entry.getKey(), aValue.toString());
                }
            }
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return rowIndex >= 0;
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            Map.Entry<String, String> entry = getRow(rowIndex);
            if (entry != null) {
                return columnIndex == 0 ? entry.getKey() : entry.getValue();
            }
            return "ERROR";
        }
    }

    private class NewPreferenceEntryDialog extends JDialog {

        private JTextField keyField;
        private JTextField valueField;
        private JButton addButton;
        private JButton cancelButton;
        boolean canceled = true;

        public NewPreferenceEntryDialog() {
            setTitle("New Preference Entry");
            setModal(true);
            setResizable(false);
            ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(7, 7, 7, 7));

            setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();

            add(new JLabel("Key: "), c);
            c.gridx = 1;
            add(keyField = new JTextField(10), c);

            c.gridx = 0;
            c.gridy = 1;
            add(new JLabel("Value: "), c);
            c.gridx = 1;
            add(valueField = new JTextField(10), c);

            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new GridLayout(0, 2));
            buttonPanel.add(addButton = new JButton("Add"), c);
            addButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    if (!values.containsKey(getKey())) {
                        if (model.validateKey(getKey()) && model.validateValue(getValue())) {
                            canceled = false;
                            dispose();
                        }
                    } else {
                        JOptionPane.showMessageDialog(RobotPreferences.this, "An entry with the key " + getKey() + " already exists", "Duplicate Key", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            getRootPane().setDefaultButton(addButton);
            
            buttonPanel.add(cancelButton = new JButton("Cancel"), c);
            cancelButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            });

            c.gridx = 0;
            c.gridy = 2;
            c.gridwidth = 2;
            add(buttonPanel, c);

            pack();
        }

        public void show(Point center) {
            setLocation((int) (center.getX() - getWidth() / 2), (int) (center.getY() - getHeight() / 2));
            setVisible(true);
        }

        public boolean isCanceled() {
            return canceled;
        }

        public String getKey() {
            return keyField.getText();
        }

        public String getValue() {
            return valueField.getText();
        }
    }

}
