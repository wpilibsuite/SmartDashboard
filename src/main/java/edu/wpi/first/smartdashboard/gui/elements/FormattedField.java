package edu.wpi.first.smartdashboard.gui.elements;

import edu.wpi.first.smartdashboard.gui.Widget;
import edu.wpi.first.smartdashboard.properties.ColorProperty;
import edu.wpi.first.smartdashboard.properties.IntegerProperty;
import edu.wpi.first.smartdashboard.properties.Property;
import edu.wpi.first.smartdashboard.types.DataType;
import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;

/**
 * Implements a simple text box UI element with a name label.
 *
 * @author pmalmsten
 */
public class FormattedField extends Widget {

  public static final DataType[] TYPES = {DataType.BASIC};

  // All three defaults will change once the init method is called
  public final ColorProperty foreground = new ColorProperty(this, "Foreground");
  public final ColorProperty background = new ColorProperty(this, "Background");
  public final IntegerProperty fontSize = new IntegerProperty(this, "Font Size", 12);


  protected JFormattedTextField valueField;

  public void init() {
    setLayout(new BorderLayout());

    final JLabel nameLabel = new JLabel(getFieldName());
    valueField = new JFormattedTextField();

    update(foreground, valueField.getForeground());
    update(background, valueField.getBackground());
    update(fontSize, valueField.getFont().getSize());

    valueField.setEditable(false);
    valueField.setColumns(10);

    add(nameLabel, BorderLayout.LINE_START);
    add(valueField, BorderLayout.CENTER);
    revalidate();
    repaint();
  }

  @Override
  public void setValue(Object value) {
    valueField.setValue(value);
    revalidate();
    repaint();
  }

  @Override
  public void propertyChanged(Property property) {
    if (property == foreground) {
      valueField.setForeground(foreground.getValue());
    } else if (property == background) {
      valueField.setBackground(background.getValue());
    } else if (property == fontSize) {
      valueField.setFont(new Font(valueField.getFont().getName(), valueField.getFont().getStyle(),
          fontSize.getValue()));
    }
  }
}
