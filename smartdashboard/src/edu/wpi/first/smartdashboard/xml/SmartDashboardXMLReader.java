package edu.wpi.first.smartdashboard.xml;

import edu.wpi.first.smartdashboard.livewindow.elements.LWSubsystem;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

import javax.xml.parsers.*;

import org.w3c.dom.*;

/**
 * A class used to read data from an XML save file that contains
 * the information for size and location of widgets in the 
 * SmartDashboard and LiveWindow.
 */
public class SmartDashboardXMLReader {

    /** An ArrayList of all the widgets in the XML file*/
    private final List<XMLWidget> widgets = new ArrayList<XMLWidget>();
    /** Maps a subsystem to a Map of components and their locations */
    private final Map<XMLWidget, Map<Integer, XMLWidget>> subsystems = new HashMap<XMLWidget, Map<Integer, XMLWidget>>();
    
    private List<String> hiddenFields = new ArrayList<String>();
    private Map<String, String> properties = new HashMap<String, String>();
    private boolean finishedReading = false;
    private SmartDashboardXMLReader self = this;

    private class ReaderThread extends Thread {

        File xmlFile;

        ReaderThread(String fileName) {
            xmlFile = new File(fileName);
        }

        @Override
        public void run() {
            try {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(xmlFile);
                doc.getDocumentElement().normalize();

                NodeList listSD = doc.getElementsByTagName("dashboard");
                NodeList listLW = doc.getElementsByTagName("live-window");

                if (listSD.getLength() == 0 && listLW.getLength() == 0) {
                    return;
                }

                load(listSD.item(0).getChildNodes());
                load(listLW.item(0).getChildNodes());
                
            } catch (Exception e) {
                System.out.println("Error while reading "+xmlFile);
                e.printStackTrace();
            }

            finishedReading = true;
        }
    }
    
    private void load(NodeList elements) {
        for (int e = 0; e < elements.getLength(); e++) {
            boolean isWidget = elements.item(e).getNodeName().equals("widget");
            if (isWidget || elements.item(e).getNodeName().equals("static-widget")) {
                NamedNodeMap atrib = elements.item(e).getAttributes();
                XMLWidget widget = new XMLWidget();
                System.out.println();
                for (int a = 0; a < atrib.getLength(); a++) {
                    if (atrib.item(a).getNodeName().equals("field")) {
                        widget.setField(atrib.item(a).getNodeValue());
                    } else if (atrib.item(a).getNodeName().equals("class")) {
                        widget.setClass(atrib.item(a).getNodeValue());
                    } else if (atrib.item(a).getNodeName().equals("type")) {
                        widget.setType(atrib.item(a).getNodeValue());
                    }
                }

                Map<Integer, XMLWidget> subwidgets = new TreeMap<Integer, XMLWidget>();

                NodeList values = elements.item(e).getChildNodes();
                for (int a = 0; a < values.getLength(); a++) {
                    if (values.item(a).getNodeName().trim().equals("location")) {
                        int x = 0, y = 0;
                        NamedNodeMap location = values.item(a).getAttributes();
                        for (int b = 0; b < location.getLength(); b++) {

                            if (location.item(b).getNodeName().equals("x")) {
                                x = Integer.parseInt(location.item(
                                        b).getNodeValue());
                            } else if (location.item(b).getNodeName().equals("y")) {
                                y = Integer.parseInt(location.item(
                                        b).getNodeValue());
                            }
                        }
                        widget.setLocation(new Point(x, y));
                    } else if (values.item(a).getNodeName().trim().equals("width")) {
                        widget.setWidth(Integer.parseInt(values.item(a).getChildNodes().item(0).getNodeValue()));
                    } else if (values.item(a).getNodeName().trim().equals("height")) {
                        widget.setHeight(Integer.parseInt(values.item(a).getChildNodes().item(0).getNodeValue()));
                    } else if (values.item(a).getNodeName().trim().equals("property")) {
                        NamedNodeMap propAtribs = values.item(a).getAttributes();
                        String name = null, value = null;
                        for (int b = 0; b < propAtribs.getLength(); b++) {
                            if (propAtribs.item(b).getNodeName().equals("name")) {
                                name = propAtribs.item(b).getNodeValue();
                            } else if (propAtribs.item(b).getNodeName().equals("value")) {
                                value = propAtribs.item(b).getNodeValue();
                            }
                        }
                        if (name != null && value != null) {
                            widget.addProperty(name, value);
                        }
                    } else if(values.item(a).getNodeName().trim().equals("widget")) {
                        LWSubsystem.setLoaded(self); // prevents subsystems from regenerating if widgets exist
                        XMLWidget subwidget = new XMLWidget();
                        NamedNodeMap attributes = values.item(a).getAttributes();
                        NodeList subvalues = values.item(a).getChildNodes();
                        for (int b = 0; b < attributes.getLength(); b++) {
                            if (attributes.item(b).getNodeName().equals("field")) {
                                subwidget.setField(attributes.item(b).getNodeValue());
                            } else if (attributes.item(b).getNodeName().equals("class")) {
                                subwidget.setClass(attributes.item(b).getNodeValue());
                            } else if (attributes.item(b).getNodeName().equals("type")) {
                                subwidget.setType(attributes.item(b).getNodeValue());
                            }
                        }
                        System.out.println("\nLoading subwidget \""+subwidget.getField()+"\"");
                        for(int b = 0; b < subvalues.getLength(); b++) {
                            String nodename = subvalues.item(b).getNodeName().trim();
                            if(nodename.equals("location")) {
                                int x = 0, y = 0;
                                NamedNodeMap location = subvalues.item(b).getAttributes();
                                for (int c = 0; c < location.getLength(); c++) {
                                    if (location.item(c).getNodeName().trim().equals("x")) {
                                        x = Integer.parseInt(location.item(c).getNodeValue());
                                    } else if (location.item(c).getNodeName().trim().equals("y")) {
                                        y = Integer.parseInt(location.item(c).getNodeValue());
                                    }
                                }
                                subwidget.setLocation(new Point(x, y));
                            } else if(nodename.equals("height")) {
                                subwidget.setHeight(Integer.parseInt(subvalues.item(b).getChildNodes().item(0).getNodeValue()));
                            } else if(nodename.equals("width")) {
                                subwidget.setWidth(Integer.parseInt(subvalues.item(b).getChildNodes().item(0).getNodeValue()));
                            }
                        }
                        System.out.println("\tLocation: ["+subwidget.getLocation().x+","+subwidget.getLocation().y +"]" + 
                                                "\n\tSize: ("+subwidget.getSize().width+","+subwidget.getSize().height+")");
                        subwidgets.put(subwidget.getLocation().y, subwidget);
                        subsystems.put(widget, subwidgets);
                    }
                }
                if(!widget.getElementClass().contains("livewindow.elements")) {
                    widgets.add(widget);
                }
            } else if (elements.item(e).getNodeName().equals("hidden")) {
                NamedNodeMap atrib = elements.item(e).getAttributes();
                for (int a = 0; a < atrib.getLength(); a++) {
                    if (atrib.item(a).getNodeName().equals("field")) {
                        hiddenFields.add(atrib.item(a).getNodeValue());
                    }
                }
            } else if (elements.item(e).getNodeName().equals("property")) {
                NamedNodeMap propAtribs = elements.item(e).getAttributes();
                String name = null, value = null;
                for (int b = 0; b < propAtribs.getLength(); b++) {
                    if (propAtribs.item(b).getNodeName().equals("name")) {
                        name = propAtribs.item(b).getNodeValue();
                    } else if (propAtribs.item(b).getNodeName().equals("value")) {
                        value = propAtribs.item(b).getNodeValue();
                    }
                }
                if (name != null && value != null) {
                    properties.put(name, value);
                }
            }
        }
    }

    public SmartDashboardXMLReader(String fileName)
            throws FileNotFoundException {
        self = this;
        new ReaderThread(fileName).start();
    }

    private void waitToFinish() {
        while (!finishedReading) {
            try {
                Thread.sleep(25);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public List<XMLWidget> getXMLWidgets() {
        waitToFinish();
        return widgets;
    }
    
    public Map<Integer, XMLWidget> getSubwidgetMap(XMLWidget subsystem) {
        waitToFinish();
        return subsystems.get(subsystem);
    }
    
    public Map<XMLWidget, Map<Integer, XMLWidget>> getSubsystems() {
        waitToFinish();
        return subsystems;
    }
    
    public boolean containsWidgetOfName(LWSubsystem subsystem, String name) {
        XMLWidget correspondingSystem = null;
        for(XMLWidget w : subsystems.keySet()) {
            if(((LWSubsystem)w.convertToDisplayElement()).getFieldName().equals(subsystem.getFieldName())) {
                correspondingSystem = w;
            }
        }
        if(correspondingSystem != null) {
            for(XMLWidget w : subsystems.get(correspondingSystem).values()) {
                String othername = w.getField();
                if(w == null || othername == null) return false;
                if(othername.equals(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<String> getHiddenFields() {
        waitToFinish();
        return hiddenFields;
    }

    public Map<String, String> getProperties() {
        waitToFinish();
        return properties;
    }

    public boolean isFinishedReading() {
        return finishedReading;
    }
}
