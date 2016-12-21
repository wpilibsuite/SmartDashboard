package edu.wpi.first.smartdashboard.types.named;

import edu.wpi.first.smartdashboard.gui.elements.Button;
import edu.wpi.first.smartdashboard.types.NamedDataType;

/**
 * @author Joe Grinstead
 */
public class ButtonType extends NamedDataType {

  public static final String LABEL = "Button";

  private ButtonType() {
    super(LABEL, Button.class);
  }

  public static NamedDataType get() {
    if (NamedDataType.get(LABEL) != null) {
      return NamedDataType.get(LABEL);
    } else {
      return new ButtonType();
    }
  }
}
