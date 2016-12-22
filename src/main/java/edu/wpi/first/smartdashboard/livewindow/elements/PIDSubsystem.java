package edu.wpi.first.smartdashboard.livewindow.elements;

import edu.wpi.first.smartdashboard.gui.elements.PIDEditor;
import edu.wpi.first.smartdashboard.types.DataType;
import edu.wpi.first.smartdashboard.types.named.PIDSubsystemType;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Sam Carlberg
 */
public class PIDSubsystem extends LWSubsystem {

  public static final DataType[] TYPES = {PIDSubsystemType.get()};

  private PIDEditor pidController;

  @Override
  public void init() {
    System.out.println("PIDSubsystem init()");
    super.init();
    try {
      pidController
          = (PIDEditor) DataType.getType("PIDController", true).getDefault().newInstance();
      pidController.setFieldName("Controller");
      pidController.setType(DataType.getType("PIDController", true));
      System.out.println("PIDSubsystem table=" + table);
      pidController.setValue(table);
      pidController.init();
      addWidget(pidController);
    } catch (InstantiationException | IllegalAccessException ex) {
      Logger.getLogger(PIDSubsystem.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  @Override
  public void setValue(Object value) {
    System.out.println("PIDSubsystem setValue(value=" + value + ")");
    super.setValue(value);
    pidController.setValue(value);
  }

}
