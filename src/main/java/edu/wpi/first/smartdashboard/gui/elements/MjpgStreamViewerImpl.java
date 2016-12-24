package edu.wpi.first.smartdashboard.gui.elements;

import edu.wpi.first.smartdashboard.properties.Property;
import edu.wpi.first.smartdashboard.properties.StringProperty;
import java.util.stream.Stream;

public class MjpgStreamViewerImpl extends MjpgStreamViewer {

  public static final String NAME = "MJPG Stream Viewer";

  public final StringProperty urlProperty = new StringProperty(this, "MJPG Server URL", "");

  private String url = "";

  @Override
  public void onInit() {
    url = STREAM_PREFIX + urlProperty.getValue();
  }

  @Override
  public void onPropertyChanged(Property property) {
    if (property == urlProperty) {
      url = STREAM_PREFIX + urlProperty.getValue();
      cameraChanged();
    }
  }

  @Override
  public Stream<String> streamPossibleCameraUrls() {
    return Stream.of(url);
  }

}
