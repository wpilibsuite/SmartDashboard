package edu.wpi.first.smartdashboard.xml;

import edu.wpi.first.smartdashboard.extensions.FileSniffer;
import edu.wpi.first.smartdashboard.gui.DisplayElement;
import edu.wpi.first.smartdashboard.gui.Widget;
import edu.wpi.first.smartdashboard.properties.Property;
import edu.wpi.first.smartdashboard.types.DataType;
import java.awt.Dimension;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class XMLWidget {

  Point location = null;
  Dimension size = null;
  HashMap<String, String> properties = new HashMap<String, String>();
  String field;
  String className = "";
  DataType type;

  public XMLWidget() {
  }

  public void setLocation(Point p) {
    location = p;
  }

  public void setSize(Dimension d) {
    size = d;
  }

  public void setClass(String c) {
    className = c;
  }

  public void setField(String f) {
    field = f;
  }

  public void setType(String type) {
    this.type = DataType.getType(type, false);
  }

  public void setWidth(int width) {
    if (size != null) {
      size.width = width;
    } else {
      size = new Dimension(width, -1);
    }
  }

  public void setHeight(int height) {
    if (size != null) {
      size.height = height;
    } else {
      size = new Dimension(-1, height);
    }
  }

  public void addProperty(String name, String value) {
    properties.put(name, value);
  }

  public boolean hasLocation() {
    return location != null;
  }

  public boolean hasSize() {
    return size != null;
  }

  public Map<String, String> getProperties() {
    return properties;
  }

  public Dimension getSize() {
    return size;
  }

  public DataType getType() {
    return type;
  }

  public Point getLocation() {
    return location;
  }

  public String getElementClass() {
    return className;
  }

  public String getField() {
    return field;
  }

  public DisplayElement convertToDisplayElement() {
    DisplayElement element = null;
    try {
      element = (DisplayElement) FileSniffer.classLoader.loadClass(className).newInstance();
      if (field != null) {
        ((Widget) element).setFieldName(field);
      }
      if (size != null) {
        element.setSavedSize(size);
      }
      if (location != null) {
        element.setSavedLocation(location);
      }
      for (String key : properties.keySet()) {
        Property property = element.getProperties().get(key);
        if (property != null) {
          property.setSaveValue(properties.get(key));
        }
      }
    } catch (Exception ex) {
      Logger.getLogger(XMLWidget.class.getName()).log(Level.SEVERE, null, ex);
    }
    return element;
  }

  @Override
  public String toString() {
    //        String returnValue = "ClassName: " + className + "\nField: " + field + "\nSize: " +
    // size
    //                + "\nLocation: " + location + "\nProperties";
    //
    //        for (String s : properties.keySet()) {
    //            String p = properties.get(s);
    //            returnValue = returnValue + "\n\nName: " + s
    //                    + "\nValue: " + p;
    //        }
    //
    //        return returnValue;
    //        return ((Widget)convertToDisplayElement()).getFieldName();
    return getField();
  }
}
