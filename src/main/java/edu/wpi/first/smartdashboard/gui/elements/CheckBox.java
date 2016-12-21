package edu.wpi.first.smartdashboard.gui.elements;

import edu.wpi.first.smartdashboard.gui.elements.bindings.AbstractValueWidget;
import edu.wpi.first.smartdashboard.properties.BooleanProperty;
import edu.wpi.first.smartdashboard.properties.Property;
import edu.wpi.first.smartdashboard.types.DataType;
import javax.swing.BoxLayout;
import javax.swing.JLabel;

/**
 * Implements a simple text box UI element with a name label.
 *
 * @author pmalmsten
 */
public class CheckBox extends AbstractValueWidget {

  public static final DataType[] TYPES = {DataType.BOOLEAN};

  public final BooleanProperty editable = new BooleanProperty(this, "Editable", true);

  private EditableBooleanValueCheckBox valueField;

  public void init() {
    setResizable(false);

    setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

    JLabel nameLabel = new JLabel(getFieldName());
    valueField = new EditableBooleanValueCheckBox(getFieldName());

    add(nameLabel);
    add(valueField);
  }

  @Override
  public void propertyChanged(Property property) {
    if (property == editable) {
      valueField.setEnabled(editable.getValue());
    }
  }
}
