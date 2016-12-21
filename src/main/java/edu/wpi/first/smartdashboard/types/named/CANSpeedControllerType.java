package edu.wpi.first.smartdashboard.types.named;

import edu.wpi.first.smartdashboard.livewindow.elements.CANSpeedController;
import edu.wpi.first.smartdashboard.types.NamedDataType;

/**
 * @author Sam Carlberg
 */
public class CANSpeedControllerType extends NamedDataType {

  public static final String LABEL = "CANSpeedController";

  private CANSpeedControllerType() {
    super(LABEL, CANSpeedController.class);
  }

  public static NamedDataType get() {
    if (NamedDataType.get(LABEL) != null) {
      return NamedDataType.get(LABEL);
    } else {
      return new CANSpeedControllerType();
    }
  }

}
