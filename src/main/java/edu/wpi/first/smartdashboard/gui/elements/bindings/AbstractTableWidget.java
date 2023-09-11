package edu.wpi.first.smartdashboard.gui.elements.bindings;

import edu.wpi.first.smartdashboard.gui.Widget;
import edu.wpi.first.smartdashboard.livewindow.elements.LWSubsystem;
import edu.wpi.first.smartdashboard.livewindow.elements.NameTag;
import edu.wpi.first.smartdashboard.types.DataType;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEvent;
import edu.wpi.first.networktables.NetworkTableValue;
import edu.wpi.first.networktables.NetworkTable.SubTableListener;
import edu.wpi.first.networktables.NetworkTable.TableEventListener;

import java.awt.Point;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComboBox;

/**
 * An abstraction for creating a widget that wraps a network table
 *
 * @author Mitchell
 */
public abstract class AbstractTableWidget extends Widget implements TableEventListener, SubTableListener {
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

      if (this.table != null) {
        this.table.removeListener(tableListenerHandle); 
      }

      this.table = table;
      if (table != null) {
        entryListenerHandle = table.addListener(
            EnumSet.of(NetworkTableEvent.Kind.kImmediate, NetworkTableEvent.Kind.kValueAll),
            this);
        if (listenSubtables) {
          table.addSubTableListener(this);
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
  public void accept(NetworkTable source, String key, NetworkTableEvent event) {
    boolean isNew = event.is(NetworkTableEvent.Kind.kPublish);
    // Some worry here that the publish event could be passed separately
    // from the value event in which case the isNew logic won't work. But
    // since generally keys don't exist in NTs separately from values it is
    // probably ok.

    // (Later): above concern was actaully valid: publish events do not include the published value.
    // However, the xxxChanged methods already track for themselves whether a value is new or not
    // so apparently nothing needs to be done for a publish event. So just return in that case.
    if (event.valueData == null) { return; }
    // We'll check for kValueAll but it should not be necessary -- he says optimistically.
    NetworkTableValue value = event.valueData.value;
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
