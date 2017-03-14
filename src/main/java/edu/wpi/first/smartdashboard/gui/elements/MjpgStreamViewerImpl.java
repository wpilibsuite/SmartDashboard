package edu.wpi.first.smartdashboard.gui.elements;

import edu.wpi.first.smartdashboard.properties.Property;
import edu.wpi.first.smartdashboard.properties.StringProperty;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.first.wpilibj.tables.ITableListener;
import java.util.stream.Stream;

public class MjpgStreamViewerImpl extends MjpgStreamViewer {

  public static final String NAME = "MJPG Stream Viewer";

  public final StringProperty urlProperty = new StringProperty(this, "MJPG Server URL", "");
  public final StringProperty selectedUrlPathProperty
      = new StringProperty(this, "Selected URL NT Path", "");

  private ITable rootTable = NetworkTable.getTable("");
  private ITableListener selectedUrlPathListener
      = (source, key, value, isNew) -> urlProperty.setValue(value);

  private String url = "";

  @Override
  public void onInit() {
    url = STREAM_PREFIX + urlProperty.getValue();

    rootTable.addTableListener(selectedUrlPathProperty.getValue(),
        selectedUrlPathListener, true);
  }

  @Override
  public void onPropertyChanged(Property property) {
    if (property == urlProperty) {
      url = STREAM_PREFIX + urlProperty.getValue();
      cameraChanged();
    } else if (property == selectedUrlPathProperty) {
      rootTable.removeTableListener(selectedUrlPathListener);
      rootTable.addTableListener(selectedUrlPathProperty.getValue(),
          selectedUrlPathListener, true);
    }
  }

  @Override
  public Stream<String> streamPossibleCameraUrls() {
    return Stream.of(url);
  }

}
