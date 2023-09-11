package edu.wpi.first.smartdashboard.types.named;

import edu.wpi.first.smartdashboard.types.NamedDataType;
import edu.wpi.first.smartdashboard.gui.elements.Scheduler;

/**
 * @author Joe Grinstead
 */
public class SchedulerType extends NamedDataType {

  public static final String LABEL = "Scheduler";

  private SchedulerType() {
    super(LABEL, Scheduler.class);
  }

  public static NamedDataType get() {
    if (NamedDataType.get(LABEL) != null) {
      return NamedDataType.get(LABEL);
    } else {
      return new SchedulerType();
    }
  }
}
