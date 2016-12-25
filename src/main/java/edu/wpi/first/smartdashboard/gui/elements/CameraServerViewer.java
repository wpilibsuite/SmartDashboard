package edu.wpi.first.smartdashboard.gui.elements;

import edu.wpi.first.smartdashboard.properties.MultiProperty;
import edu.wpi.first.smartdashboard.properties.Property;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.tables.ITable;
import java.util.Arrays;
import java.util.stream.Stream;

public class CameraServerViewer extends MjpgStreamViewer {

  public static final String NAME = "CameraServer Stream Viewer";

  private static final String STREAMS_KEY = "streams";

  public final MultiProperty cameraProperty = new MultiProperty(this, "Camera Choice");

  private ITable cameraTable;

  @Override
  public void onInit() {
    NetworkTable.getTable("CameraPublisher").addSubTableListener(((source, key, value, isNew) -> {
      cameraProperty.add(key, value);
      if (cameraTable == null
          && (cameraProperty.getSavedValue().isEmpty()
          || key.equals(cameraProperty.getSavedValue()))) {
        cameraTable = (ITable) value;
      }
    }));
  }

  @Override
  public void onPropertyChanged(Property property) {
    if (property == cameraProperty) {
      cameraTable = (ITable) cameraProperty.getValue();
      cameraChanged();
    }
  }

  @Override
  public Stream<String> streamPossibleCameraUrls() {
    if (cameraTable == null) {
      return Stream.empty();
    }

    return Arrays.stream(cameraTable.getStringArray(STREAMS_KEY, new String[0])).map(s -> {
      if (NetworkTable.connections().length > 0) {
        return s.replaceFirst("roboRIO-\\d+-FRC.*(?=:)", NetworkTable.connections()[0].remote_ip);
      }
      return s;
    });
  }

}
