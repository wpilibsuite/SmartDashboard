package edu.wpi.first.smartdashboard.gui.elements.bindings;

import edu.wpi.first.smartdashboard.gui.Widget;
import edu.wpi.first.smartdashboard.livewindow.elements.LWSubsystem;
import edu.wpi.first.smartdashboard.livewindow.elements.NameTag;
import edu.wpi.first.smartdashboard.types.DataType;
import edu.wpi.first.networktables.EntryListenerFlags;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableValue;
import edu.wpi.first.networktables.TableEntryListener;
import edu.wpi.first.networktables.TableListener;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JComboBox;

/**
 * An abstraction for creating a widget that wraps a network table
 *
 * @author Mitchell
 */
public abstract class AbstractTableWidget extends Widget implements TableEntryListener, TableListener {
  protected NetworkTable table;
  protected int entryListenerHandle;
  protected int tableListenerHandle;

  /**
   * A NameTag for this widget.
   */
  public NameTag nameTag;

  private final boolean listenSubtables;

  public AbstractTableWidget() {
    this(false);
  }

  protected AbstractTableWidget(boolean listenSubtables) {
    this.listenSubtables = listenSubtables;
  }

  @Override
  public void setValue(Object value) {
    if (value instanceof NetworkTable) {
      NetworkTable table = (NetworkTable) value;

      if (table != null) {
        // should this be this.table.removeTableListener?
        table.removeTableListener(tableListenerHandle);
      }

      this.table = table;
      if (table != null) {
        entryListenerHandle = table.addEntryListener(this,
            EntryListenerFlags.kImmediate | EntryListenerFlags.kLocal 
            | EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

        if (listenSubtables) {
          tableListenerHandle = table.addSubTableListener(this, true);
        }
      }
    }
  }

  public void setField(String name, Widget element, DataType type, Object value, LWSubsystem
      subsystem, Point point) {
    element.setFieldName(name);
    element.setType(type);
    element.init();
    if (value != null) {
      element.setValue(value);
    }
    subsystem.addWidget(element);
  }

  private Map<String, List<BooleanBindable>> booleanFields = new HashMap<>();
  private Map<String, List<NumberBindable>> numberFields = new HashMap<>();
  private Map<String, List<StringBindable>> stringFields = new HashMap<>();

  @Override
  public void valueChanged(NetworkTable source, String key, NetworkTableEntry entry,
                           NetworkTableValue value, int flags) {
    boolean isNew = (flags & EntryListenerFlags.kNew) != 0;
    if (value.isBoolean()) {
      booleanChanged(source, key, value.getBoolean(), isNew);
    }
    if (value.isDouble()) {
      doubleChanged(source, key, value.getDouble(), isNew);
    }
    if (value.isString()) {
      stringChanged(source, key, value.getString(), isNew);
    }
  }

  public void booleanChanged(NetworkTable source, String key, boolean value, boolean isNew) {
    if (!booleanFields.containsKey(key)) {
      booleanFields.put(key, new ArrayList<>());
    }
    List<BooleanBindable> field = booleanFields.get(key);
    field.stream().forEach(bindable -> bindable.setBindableValue(value));
  }

  public void doubleChanged(NetworkTable source, String key, double value, boolean isNew) {
    if (!numberFields.containsKey(key)) {
      numberFields.put(key, new ArrayList<>());
    }
    List<NumberBindable> field = numberFields.get(key);
    field.stream().forEach(bindable -> bindable.setBindableValue(value));
  }

  public void stringChanged(NetworkTable source, String key, String value, boolean isNew) {
    if (!stringFields.containsKey(key)) {
      stringFields.put(key, new ArrayList<>());
    }
    List<StringBindable> field = stringFields.get(key);
    field.stream().forEach(bindable -> bindable.setBindableValue(value));
  }

  @Override
  public void tableCreated(NetworkTable parent, String name, NetworkTable newTable) {
  }


  public MultiTypeBindable getTableEntryBindable(final String key) {
    return new MultiTypeBindable() {
      @Override
      public void setBindableValue(boolean value) {
        if (table != null) {
          table.getEntry(key).setBoolean(value);
        }
      }

      @Override
      public void setBindableValue(double value) {
        if (table != null) {
          table.getEntry(key).setNumber(value);
        }
      }

      @Override
      public void setBindableValue(String value) {
        if (table != null) {
          table.getEntry(key).setString(value);
        }
      }
    };
  }


  protected void setBooleanBinding(String key, BooleanBindable displayer) {
    if (!booleanFields.containsKey(key)) {
      booleanFields.put(key, new ArrayList<>());
    }
    booleanFields.get(key).add(displayer);
  }

  protected void setNumberBinding(String key, NumberBindable displayer) {
    if (!numberFields.containsKey(key)) {
      numberFields.put(key, new ArrayList<>());
    }
    numberFields.get(key).add(displayer);
  }

  protected void setStringBinding(String key, StringBindable displayer, String defaultValue) {
    displayer.setBindableValue(defaultValue);
    if (!stringFields.containsKey(key)) {
      stringFields.put(key, new ArrayList<>());
    }
    stringFields.get(key).add(displayer);
  }


  public class BooleanTableCheckBox extends BindableBooleanCheckBox {
    public BooleanTableCheckBox(final String key) {
      super(getTableEntryBindable(key));
      setBooleanBinding(key, this);
    }
  }

  public class BooleanTableField extends BindableBooleanField {
    public BooleanTableField(final String key) {
      super(getTableEntryBindable(key));
      setBooleanBinding(key, this);
    }
  }

  public class NumberTableField extends BindableNumberField {
    public NumberTableField(final String key) {
      super(getTableEntryBindable(key));
      setNumberBinding(key, this);
    }
  }

  public class StringTableField extends BindableStringField {
    public StringTableField(final String key) {
      super(getTableEntryBindable(key));
      setStringBinding(key, this, "");
    }
  }

  public class StringTableComboBox extends JComboBox<String> implements StringBindable {
    public StringTableComboBox(final String key, String... items) {
      super(items);
      getTableEntryBindable(key);
      setStringBinding(key, this, "");
      addActionListener(e -> table.getEntry(key).setString((String) getSelectedItem()));
    }

    @Override
    public void setBindableValue(String value) {
      setSelectedItem(value);
    }
  }

  public class NumberTableComboBox<E> extends JComboBox<E> implements NumberBindable {
    public NumberTableComboBox(final String key, E... items) {
      super(items);
      getTableEntryBindable(key);
      setNumberBinding(key, this);
      addActionListener(e -> table.getEntry(key).setNumber(getSelectedIndex()));
    }

    @Override
    public void setBindableValue(double value) {
      if ((int) value == value && value >= 0 && value < getItemCount()) {
        setSelectedIndex((int) value);
      }
    }
  }
}
