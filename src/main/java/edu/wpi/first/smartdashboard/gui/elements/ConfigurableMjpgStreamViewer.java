package edu.wpi.first.smartdashboard.gui.elements;

import edu.wpi.first.smartdashboard.properties.StringProperty;
import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.first.smartdashboard.robot.Robot;

public class ConfigurableMjpgStreamViewer extends MjpgStreamViewerImpl {
  static int viewerCount = 0;
  static final String NAME = "Configurable MJPG Stream Viewer";

  public final StringProperty nameProperty = new StringProperty(this, "Name", "stream" + viewerCount++);

  @Override
  public void onInit() {
    super.onInit();
    
    ITable table = Robot.getTable();
    table.addTableListener((source, key, value, isNew) -> {
      if(key.equals(nameProperty.getValue() + "-url")) {
        String newUrl = (String) value;
        urlProperty.setValue(newUrl);
        onPropertyChanged(urlProperty);
      }
    });
  }
}
