package edu.wpi.first.smartdashboard.gui.elements;

import edu.wpi.first.smartdashboard.properties.MultiProperty;
import edu.wpi.first.smartdashboard.properties.Property;
import edu.wpi.first.smartdashboard.properties.StringProperty;
import edu.wpi.first.networktables.EntryListenerFlags;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.TableEntryListener;
import java.util.Arrays;
import java.util.stream.Stream;

public class CameraServerViewer extends MjpgStreamViewer {

  public static final String NAME = "CameraServer Stream Viewer";

  private static final String STREAMS_KEY = "streams";

  public final MultiProperty cameraProperty = new MultiProperty(this, "Camera Choice");
  public final StringProperty selectedCameraPathProperty
      = new StringProperty(this, "Selected Camera Path", "");

  private NetworkTable cameraTable;
  private NetworkTable rootTable = NetworkTableInstance.getDefault().getTable("");
  private TableEntryListener selectedCameraPathListener
      = (source, key, entry, value, flags) -> cameraProperty.setValue(value);
  private int listenerHandle;

  @Override
  public void onInit() {
    rootTable.getSubTable("CameraPublisher").addSubTableListener(
      ((NetworkTable source, String key, NetworkTable subtable) -> {
        cameraProperty.add(key, subtable);
        if (cameraTable == null
            && (cameraProperty.getSavedValue().isEmpty()
            || key.equals(cameraProperty.getSavedValue()))) {
          cameraTable = subtable;
        }
      }), true
    );

    listenerHandle = rootTable.addEntryListener(selectedCameraPathProperty.getValue(),
        selectedCameraPathListener, EntryListenerFlags.kLocal | EntryListenerFlags.kImmediate 
        | EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);
  }  

  @Override
  public void onPropertyChanged(Property property) {
    if (property == cameraProperty) {
      cameraTable = (NetworkTable) cameraProperty.getValue();
      cameraChanged();
    } else if (property == selectedCameraPathProperty) {
      rootTable.removeTableListener(listenerHandle);
      listenerHandle = rootTable.addEntryListener(selectedCameraPathProperty.getValue(),
          selectedCameraPathListener, 
          EntryListenerFlags.kLocal | EntryListenerFlags.kImmediate 
        | EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);
    }
  }

  @Override
  public Stream<String> streamPossibleCameraUrls() {
    if (cameraTable == null) {
      return Stream.empty();
    }

    return Arrays.stream(cameraTable.getEntry(STREAMS_KEY).getStringArray(new String[0])).map(s -> {
      if (NetworkTableInstance.getDefault().getConnections().length > 0) {
        return s.replaceFirst("roboRIO-\\d+-FRC.*(?=:)", 
                              NetworkTableInstance.getDefault().getConnections()[0].remote_ip);
      }
      return s;
    });
  }

}
