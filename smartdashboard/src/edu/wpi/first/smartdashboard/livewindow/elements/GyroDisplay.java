/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.smartdashboard.livewindow.elements;

import edu.wpi.first.smartdashboard.gui.elements.bindings.AbstractTableWidget;
import edu.wpi.first.smartdashboard.gui.elements.Compass;
import edu.wpi.first.smartdashboard.properties.Property;
import edu.wpi.first.smartdashboard.types.DataType;
import edu.wpi.first.smartdashboard.types.named.CompassType;
import edu.wpi.first.smartdashboard.types.named.LWGyroType;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;


/**
 * Displays data from a Gyro or Compass.
 * @author Sam
 */
public class GyroDisplay extends AbstractTableWidget { // TODO: get the compass working correctly
    
    public static final DataType[] TYPES = {LWGyroType.get(), 
                                            CompassType.get()};
    
    /* A Compass display that graphically shows which direction the sensor is facing */
    private final Compass compass = new Compass();
    /** A text display that shows the value of the sensor. */
    private final UneditableNumberField feedback = new UneditableNumberField();
    /** Options in a ComboBox for which display to show. */
    private final String[] names = {"Display as Text", "Display as Compass"};
    private final JComboBox menu = new JComboBox(names);

    @Override
    public void init() {
        
        setLayout(new GridBagLayout());
        final GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        
        final GyroDisplay self = this;
        nameTag = new NameTag(getFieldName());
        
        setNumberBinding("Value", new NumberMultiBindable(feedback, compass));
        
        compass.init();
        
        menu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JComboBox box = (JComboBox) e.getSource();
                String name = box.getSelectedItem().toString();
                if(name.equals(names[1])) {
                    self.remove(feedback);
                    c.gridx = 0;
                    c.gridy = 2;
                    c.weightx = 2;
                    self.add(compass, c);
                    revalidate();
                    repaint();
                } else if(name.equals(names[0])) {
                    self.remove(compass);
                    c.gridx = 0;
                    c.gridy = 2;
                    c.weightx = 2;
                    self.add(feedback, c);
                    revalidate();
                    repaint();
                }
            }
        });
        
        
        c.gridx = 0;
        add(nameTag, c);
        c.gridy = 1;
        add(menu, c);
        c.gridy = 2;
        c.weightx = 2;
        add(feedback, c);
        
    }
    
    public void propertyChanged(Property property) {
        
    }
    
}
