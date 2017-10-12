package edu.wpi.first.smartdashboard.gui.elements;

import edu.wpi.first.networktables.PersistentException;
import edu.wpi.first.smartdashboard.gui.StaticWidget;
import edu.wpi.first.smartdashboard.properties.Property;
import edu.wpi.first.smartdashboard.robot.Robot;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.first.wpilibj.tables.ITableListener;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import org.jfree.ui.ExtensionFileFilter;

/**
 * @author Joe Grinstead
 */
public class RobotPreferences extends StaticWidget implements ITableListener {

  public static final String NAME = "Robot Preferences";
  private JTable table;
  private PreferenceTableModel model;
  private Map<String, Object> values;
  private JButton add;
  private JButton remove;
  private JButton save;
  private JButton load;

  @Override
  public void init() {
    add = new JButton("Add");
    add.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e) {
        NewPreferenceEntryDialog dialog = new NewPreferenceEntryDialog();
        dialog.show(remove.getLocationOnScreen());
        if (!dialog.isCanceled()) {
          model.putString(dialog.getKey(), dialog.getValue(), dialog.getValueType());
        }
      }
    });

    remove = new JButton("Remove");
    remove.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e) {
        if (table.isEditing()) {
          table.getCellEditor().cancelCellEditing();
        }
        int viewIndex = table.getSelectedRow(); 
        if (viewIndex != -1) {
          int modelIndex = table.convertRowIndexToModel(viewIndex);
          Map.Entry<String, Object> entry = model.getRow(modelIndex);
          if (entry != null) {
            model.delete(entry.getKey());
          }
        }
      }
    });

    save = new JButton("Save");
    save.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e) {
        if (table.isEditing()) {
          table.getCellEditor().cancelCellEditing();
        }
        JFileChooser fc = new JFileChooser(".");
        fc.addChoosableFileFilter(new ExtensionFileFilter("INI File", ".ini"));
        fc.setApproveButtonText("Save");
        fc.setDialogTitle("Save Preferences As...");

        fc.setMultiSelectionEnabled(false);
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

        if (fc.showOpenDialog(RobotPreferences.this) == JFileChooser.APPROVE_OPTION) {
          String filepath = fc.getSelectedFile().getAbsolutePath();
          if (!filepath.endsWith(".ini")) {
            filepath = filepath + ".ini";
          }
          model.save(filepath);
        }
      }
    });

    load = new JButton("Load");
    load.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e) {
        if (table.isEditing()) {
          table.getCellEditor().cancelCellEditing();
        }
        JFileChooser fc = new JFileChooser(".");
        fc.addChoosableFileFilter(new ExtensionFileFilter("INI File", ".ini"));
        fc.setMultiSelectionEnabled(false);
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setApproveButtonText("Load");
        fc.setDialogTitle("Load Preferences");

        if (fc.showOpenDialog(RobotPreferences.this) == JFileChooser.APPROVE_OPTION) {
          model.load(fc.getSelectedFile().getAbsolutePath());
        }
      }
    });

    values = new LinkedHashMap<String, Object>();

    Robot.getPreferences().addTableListenerEx(this,
        ITable.NOTIFY_IMMEDIATE | ITable.NOTIFY_LOCAL | ITable.NOTIFY_NEW | ITable.NOTIFY_DELETE
            | ITable.NOTIFY_UPDATE);

    model = new PreferenceTableModel();

    table = new JTable(model);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    table.getTableHeader().setReorderingAllowed(false);
    table.setAutoCreateRowSorter(true);


    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new GridLayout(0, 4));
    buttonPanel.add(add);
    buttonPanel.add(remove);
    buttonPanel.add(save);
    buttonPanel.add(load);

    JPanel controlPanel = new JPanel();
    controlPanel.setLayout(new BorderLayout());
    controlPanel.add(buttonPanel, BorderLayout.NORTH);

    setLayout(new BorderLayout());
    JScrollPane tableScrollPane
        = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane
        .HORIZONTAL_SCROLLBAR_NEVER);
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
  }

  @Override
  public void valueChangedEx(ITable source, String key, Object value, int flags) {
    if ((flags & ITable.NOTIFY_DELETE) != 0) {
      values.remove(key);
    } else {
      values.put(key, value);
    }

    if (model != null) {
      model.fireTableDataChanged();
    }
  }

  private static final String[] typeNames
      = new String[]{"Boolean", "Number", "String", "Raw", "Boolean[]", "Number[]", "String[]"};

  private static String getTypeName(Object value) {
    if (value instanceof Boolean) {
      return typeNames[0];
    }
    if (value instanceof Double) {
      return typeNames[1];
    }
    if (value instanceof String) {
      return typeNames[2];
    }
    if (value instanceof byte[]) {
      return typeNames[3];
    }
    if (value instanceof boolean[]) {
      return typeNames[4].substring(0, 8) + ((boolean[]) value).length + "]";
    }
    if (value instanceof double[]) {
      return typeNames[5].substring(0, 7) + ((double[]) value).length + "]";
    }
    if (value instanceof String[]) {
      return typeNames[6].substring(0, 7) + ((String[]) value).length + "]";
    }
    return "ERROR";
  }

  private static int getTypeIndex(String name) {
    if (name.equals("Boolean")) {
      return 0;
    }
    if (name.equals("Number")) {
      return 1;
    }
    if (name.equals("String")) {
      return 2;
    }
    if (name.equals("Raw")) {
      return 3;
    }
    if (name.startsWith("Boolean[")) {
      return 4;
    }
    if (name.startsWith("Number[")) {
      return 5;
    }
    if (name.startsWith("String[")) {
      return 6;
    }
    return -1;
  }

  private static String hex(char ch) {
    return Integer.toHexString(ch).toLowerCase(Locale.ENGLISH);
  }

  private static void escapeString(StringBuilder out, String str, boolean inArray) {
    int sz = str.length();
    for (int i = 0; i < sz; i++) {
      char ch = str.charAt(i);
      if (ch < 32) {
        switch (ch) {
          case '\b':
            out.append("\\b");
            break;
          case '\n':
            out.append("\\n");
            break;
          case '\t':
            out.append("\\t");
            break;
          case '\f':
            out.append("\\f");
            break;
          case '\r':
            out.append("\\r");
            break;
          default:
            if (ch > 0xf) {
              out.append("\\u00" + hex(ch));
            } else {
              out.append("\\u000" + hex(ch));
            }
            break;
        }
      } else {
        switch (ch) {
          case ',':
          case ']':
            if (inArray) {
              out.append('\\');
            }
            out.append(ch);
            break;
          case '\\':
            out.append("\\\\");
            break;
          default:
            out.append(ch);
            break;
        }
      }
    }
  }

  private static String escapeString(String str, boolean inArray) {
    StringBuilder out = new StringBuilder();
    escapeString(out, str, inArray);
    return out.toString();
  }

  private static void unescapeString(StringBuilder out, String str) {
    int sz = str.length();
    StringBuilder unicode = new StringBuilder(4);
    boolean hadSlash = false;
    boolean inUnicode = false;
    for (int i = 0; i < sz; i++) {
      char ch = str.charAt(i);
      if (inUnicode) {
        // if in unicode, then we're reading unicode
        // values in somehow
        unicode.append(ch);
        if (unicode.length() == 4) {
          // unicode now contains the four hex digits
          // which represents our unicode character
          try {
            int value = Integer.parseInt(unicode.toString(), 16);
            out.append((char) value);
            unicode.setLength(0);
            inUnicode = false;
            hadSlash = false;
          } catch (NumberFormatException nfe) {
            throw new NumberFormatException("Unable to parse unicode value: " + unicode);
          }
        }
        continue;
      }
      if (hadSlash) {
        // handle an escaped value
        hadSlash = false;
        switch (ch) {
          case 'r':
            out.append('\r');
            break;
          case 'f':
            out.append('\f');
            break;
          case 't':
            out.append('\t');
            break;
          case 'n':
            out.append('\n');
            break;
          case 'b':
            out.append('\b');
            break;
          case 'u': {
            // uh-oh, we're in unicode country....
            inUnicode = true;
            break;
          }
          default:
            out.append(ch);
            break;
        }
        continue;
      } else if (ch == '\\') {
        hadSlash = true;
        continue;
      }
      out.append(ch);
    }
    if (hadSlash) {
      // then we're in the weird case of a \ at the end of the
      // string, let's output it anyway.
      out.append('\\');
    }
  }

  private static String unescapeString(String str) {
    StringBuilder out = new StringBuilder();
    unescapeString(out, str);
    return out.toString();
  }

  private class PreferenceTableModel extends AbstractTableModel {

    public int getRowCount() {
      return values.size();
    }

    public int getColumnCount() {
      return 3;
    }

    @Override
    public String getColumnName(int i) {
      switch (i) {
        case 0:
          return "Key";
        case 1:
          return "Value";
        case 2:
          return "Type";
        default:
          return "ERROR";
      }
    }

    public Map.Entry<String, Object> getRow(int rowIndex) {
      int row = 0;
      for (Map.Entry<String, Object> entry : values.entrySet()) {
        if (row++ == rowIndex) {
          return entry;
        }
      }
      return null;
    }

    public boolean put(String key, Object value) {
      if (validateKey(key)) {
        Robot.getPreferences().putValue(key, value);
        Robot.getPreferences().setPersistent(key);
        return true;
      }
      return false;
    }

    public boolean putString(String key, String value, String type) {
      int typeIndex = getTypeIndex(type);
      if (typeIndex < 0 || !validateKey(key)) {
        return false;
      }
      Object valueObj = validateValue(value, typeIndex);
      if (valueObj == null) {
        return false;
      }
      Robot.getPreferences().putValue(key, valueObj);
      Robot.getPreferences().setPersistent(key);
      return true;
    }

    public void delete(String key) {
      Robot.getPreferences().delete(key);
    }

    public boolean validateKey(String key) {
      if (key.isEmpty()) {
        JOptionPane.showMessageDialog(RobotPreferences.this, "The key cannot be empty", "Bad "
            + "Key", JOptionPane.ERROR_MESSAGE);
        return false;
      }
      return true;
    }

    public Object validateValue(String value, int typeIndex) {
      switch (typeIndex) {
        case 0: // Boolean
        {
          String lower = value.toLowerCase(Locale.ENGLISH);
          if (lower.equals("y") || lower.equals("yes") || lower.equals("t") || lower.equals("true")
              || lower.equals("on") || lower.equals("1")) {
            return new Boolean(true);
          } else if (lower.equals("n") || lower.equals("no") || lower.equals("f")
              || lower.equals("false") || lower.equals("off") || lower.equals("0")) {
            return new Boolean(false);
          } else {
            JOptionPane.showMessageDialog(RobotPreferences.this, "Invalid boolean value; expected"
                + " one of yes, true, 1, no, false, 0", "Bad Value", JOptionPane.ERROR_MESSAGE);
          }
          break;
        }
        case 1: // Number
          try {
            return Double.parseDouble(value);
          } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(RobotPreferences.this, "Invalid number value", "Bad "
                + "Value", JOptionPane.ERROR_MESSAGE);
          }
          break;
        case 2: // String
          try {
            return unescapeString(value);
          } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(RobotPreferences.this,
                "Invalid string: " + e.getMessage(), "Bad Value", JOptionPane.ERROR_MESSAGE);
          }
        // TODO: Should this be a fallthrough?
        case 3: // Raw
        {
          value = value.trim();
          if (value.equals("[]")) {
            return new byte[0];
          }
          if (!value.startsWith("[")) {
            JOptionPane.showMessageDialog(RobotPreferences.this, "Invalid array: missing [", "Bad"
                + " Value", JOptionPane.ERROR_MESSAGE);
            break;
          }
          if (!value.endsWith("]")) {
            JOptionPane.showMessageDialog(RobotPreferences.this, "Invalid array: missing ]", "Bad"
                + " Value", JOptionPane.ERROR_MESSAGE);
            break;
          }

          String[] arr = value.substring(1, value.length() - 1).split(",");
          byte[] barr = new byte[arr.length];
          for (int i = 0; i < arr.length; i++) {
            try {
              barr[i] = Byte.parseByte(arr[i].trim());
            } catch (NumberFormatException e) {
              JOptionPane.showMessageDialog(RobotPreferences.this,
                  "Invalid number value at index " + i + ": '" + arr[i].trim()
                      + "'", "Bad Value", JOptionPane.ERROR_MESSAGE);
              return null;
            }
          }
          return barr;
        }
        case 4: // Boolean[]
        {
          value = value.trim();
          if (value.equals("[]")) {
            return new boolean[0];
          }
          if (!value.startsWith("[")) {
            JOptionPane.showMessageDialog(RobotPreferences.this, "Invalid array: missing [", "Bad"
                + " Value", JOptionPane.ERROR_MESSAGE);
            break;
          }
          if (!value.endsWith("]")) {
            JOptionPane.showMessageDialog(RobotPreferences.this, "Invalid array: missing ]", "Bad"
                + " Value", JOptionPane.ERROR_MESSAGE);
            break;
          }

          String[] arr = value.substring(1, value.length() - 1).split(",");
          boolean[] barr = new boolean[arr.length];
          for (int i = 0; i < arr.length; i++) {
            String lower = arr[i].trim().toLowerCase(Locale.ENGLISH);
            if (lower.equals("y") || lower.equals("yes") || lower.equals("t")
                || lower.equals("true") || lower.equals("on") || lower.equals("1")) {
              barr[i] = true;
            } else if (lower.equals("n") || lower.equals("no") || lower.equals("f")
                || lower.equals("false") || lower.equals("off") || lower.equals("0")) {
              barr[i] = false;
            } else {
              JOptionPane.showMessageDialog(RobotPreferences.this,
                  "Invalid boolean value at index " + i + ": '" + arr[i].trim()
                      + "'; expected one of yes, true, 1, no, false, 0", "Bad Value", JOptionPane
                      .ERROR_MESSAGE);
              return null;
            }
          }
          return barr;
        }
        case 5: // Number[]
        {
          value = value.trim();
          if (value.equals("[]")) {
            return new double[0];
          }
          if (!value.startsWith("[")) {
            JOptionPane.showMessageDialog(RobotPreferences.this, "Invalid array: missing [", "Bad"
                + " Value", JOptionPane.ERROR_MESSAGE);
            break;
          }
          if (!value.endsWith("]")) {
            JOptionPane.showMessageDialog(RobotPreferences.this, "Invalid array: missing ]", "Bad"
                + " Value", JOptionPane.ERROR_MESSAGE);
            break;
          }

          String[] arr = value.substring(1, value.length() - 1).split(",");
          double[] darr = new double[arr.length];
          for (int i = 0; i < arr.length; i++) {
            try {
              darr[i] = Double.parseDouble(arr[i].trim());
            } catch (NumberFormatException e) {
              JOptionPane.showMessageDialog(RobotPreferences.this,
                  "Invalid number value at index " + i + ": '" + arr[i].trim()
                      + "'", "Bad Value", JOptionPane.ERROR_MESSAGE);
              return null;
            }
          }
          return darr;
        }
        case 6: // String[]
        {
          value = value.trim();
          if (value.equals("[]")) {
            return new String[0];
          }
          if (!value.startsWith("[")) {
            JOptionPane.showMessageDialog(RobotPreferences.this, "Invalid array: missing [", "Bad"
                + " Value", JOptionPane.ERROR_MESSAGE);
            break;
          }
          if (!value.endsWith("]") || (value.endsWith("\\]") && !value.endsWith("\\\\]"))) {
            JOptionPane.showMessageDialog(RobotPreferences.this, "Invalid array: missing ]", "Bad"
                + " Value", JOptionPane.ERROR_MESSAGE);
            break;
          }
          if (value.contains("]]]")) {
            JOptionPane.showMessageDialog(RobotPreferences.this, "Invalid array: unescaped ]",
                "Bad Value", JOptionPane.ERROR_MESSAGE);
            break;
          }
          // need to replace with unique string to make "\\" at end of value work
          value = value.replaceAll("\\\\\\\\", "]]]");
          // use look-behind assertion to avoid matching "\,"
          String[] arr = value.substring(1, value.length() - 1).split("(?<!\\\\),");
          for (int i = 0; i < arr.length; i++) {
            if (arr[i].startsWith(" ")) {
              arr[i] = arr[i].substring(1);
            }
            try {
              arr[i] = unescapeString(arr[i].replaceAll("]]]", "\\\\\\\\"));
            } catch (NumberFormatException e) {
              JOptionPane.showMessageDialog(RobotPreferences.this,
                  "Invalid string at index " + i + ": "
                      + e.getMessage(), "Bad Value", JOptionPane.ERROR_MESSAGE);
            }
          }
          return arr;
        }
      }
      return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
      if (columnIndex == 0) { //Key
        Map.Entry<String, Object> entry = getRow(rowIndex);
        if (entry != null) {
          String oldName = entry.getKey();
          Object value = entry.getValue();
          if (!oldName.equals(aValue.toString())) {
            if (!values.containsKey(aValue.toString())) {
              if (put(aValue.toString(), value)) {
                delete(oldName);
              }
            } else {
              JOptionPane.showMessageDialog(RobotPreferences.this, "An entry with the key " + aValue
                  + " already exists", "Duplicate Key", JOptionPane.ERROR_MESSAGE);
            }
          }
        }
      } else if (columnIndex == 1) { //Value
        Map.Entry<String, Object> entry = getRow(rowIndex);
        if (entry != null) {
          putString(entry.getKey(), aValue.toString(), getTypeName(entry.getValue()));
        }
      }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
      return rowIndex >= 0 && columnIndex < 2;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
      Map.Entry<String, Object> entry = getRow(rowIndex);
      if (entry != null) {
        switch (columnIndex) {
          case 0:
            return entry.getKey();
          case 1: {
            Object value = entry.getValue();
            if (value instanceof Boolean) {
              return ((Boolean) value).toString();
            }
            if (value instanceof Double) {
              return ((Double) value).toString();
            }
            if (value instanceof String) {
              return escapeString((String) value, false);
            }
            if (value instanceof byte[]) {
              return Arrays.toString((byte[]) value);
            }
            if (value instanceof boolean[]) {
              return Arrays.toString((boolean[]) value);
            }
            if (value instanceof double[]) {
              return Arrays.toString((double[]) value);
            }
            if (value instanceof String[]) {
              String[] a = (String[]) value;
              int imax = a.length - 1;
              if (imax == -1) {
                return "[]";
              }

              StringBuilder b = new StringBuilder();
              b.append('[');
              for (int i = 0; ; i++) {
                escapeString(b, a[i], true);
                if (i == imax) {
                  return b.append(']').toString();
                }
                b.append(", ");
              }
            }
            break;
          }
          case 2:
            return getTypeName(entry.getValue());
          default:
            break;
        }
      }
      return "ERROR";
    }

    public void save(String filename) {
      try {
        NetworkTable.savePersistent(filename);
      } catch (PersistentException e) {
        JOptionPane.showMessageDialog(RobotPreferences.this,
            "Error saving file:" + e.getMessage(), "Save Preferences", JOptionPane.ERROR_MESSAGE);
      }
    }

    public void load(String filename) {
      String[] warnings;
      try {
        warnings = NetworkTable.loadPersistent(filename);
        if (warnings.length > 0) {
          JOptionPane.showMessageDialog(RobotPreferences.this, "Warning loading file:"
              + Arrays.toString(warnings), "Load Preferences", JOptionPane.WARNING_MESSAGE);
        }
      } catch (PersistentException e) {
        JOptionPane.showMessageDialog(RobotPreferences.this,
            "Error loading file:" + e.getMessage(), "Load Preferences", JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  private class NewPreferenceEntryDialog extends JDialog {

    private JTextField keyField;
    private JComboBox<String> typeComboBox;
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
      add(new JLabel("Type: "), c);
      c.gridx = 1;
      add(typeComboBox = new JComboBox<String>(typeNames), c);

      c.gridx = 0;
      c.gridy = 2;
      add(new JLabel("Value: "), c);
      c.gridx = 1;
      add(valueField = new JTextField(10), c);

      JPanel buttonPanel = new JPanel();
      buttonPanel.setLayout(new GridLayout(0, 2));
      buttonPanel.add(addButton = new JButton("Add"), c);
      addButton.addActionListener(new ActionListener() {

        public void actionPerformed(ActionEvent e) {
          if (!values.containsKey(getKey())) {
            if (model.validateKey(getKey())
                && model.validateValue(getValue(), getTypeIndex(getValueType())) != null) {
              canceled = false;
              dispose();
            }
          } else {
            JOptionPane.showMessageDialog(RobotPreferences.this, "An entry with the key " + getKey()
                + " already exists", "Duplicate Key", JOptionPane.ERROR_MESSAGE);
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
      c.gridy = 3;
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

    public String getValueType() {
      return (String) typeComboBox.getSelectedItem();
    }

    public String getValue() {
      return valueField.getText();
    }
  }

}
