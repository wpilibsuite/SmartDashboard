/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.smartdashboard.livewindow.elements;

import edu.wpi.first.smartdashboard.gui.Widget;
import edu.wpi.first.smartdashboard.gui.elements.bindings.AbstractTableWidget;
import edu.wpi.first.smartdashboard.gui.elements.bindings.NumberBindable;
import edu.wpi.first.smartdashboard.properties.Property;
import edu.wpi.first.smartdashboard.types.DataType;
import edu.wpi.first.smartdashboard.types.named.ServoType;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JButton;

/**
 * Controls a Servo. Very similar to a {@link SpeedController}, but 
 * sends values from 0 to +180 degrees to the servo instead of -1 to +1
 * for SpeedControllers such as Victors.
 * @author Sam
 */
public class ServoController extends AbstractTableWidget implements Controller {
    
    public static final DataType[] TYPES = {ServoType.get()};
    
    private NumberSlider controller;
    /** Used to turn off the servo */
    private final JButton zeroButton = new JButton("Zero");
    
    private final String defaultText = "0.0";
    private final Widget.UneditableNumberField feedback = new Widget.UneditableNumberField();
    private final NumberBindable valueEntry = getTableEntryBindable("Value");

    @Override
    public void init() {
        nameTag = new NameTag(getFieldName());
        
    	controller = new Widget.NumberSlider(valueEntry);
    	controller.setMin(0);   // The minimum value able to be sent to the servo
    	controller.setMax(1); // The maximum value able to be sent to the servo
    	controller.setBindableValue(0.0);
        controller.setSnapToTicks(false);
        controller.setMajorTickSpacing(50);
        controller.setPaintTicks(true);
        
        feedback.setText(defaultText);
        feedback.setColumns(4);
        
        setNumberBinding("Value", new Widget.NumberMultiBindable(feedback));
        
        controller.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    zeroButton.doClick();
                }
            }
        });
        
        feedback.setEditable(false);
        zeroButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                reset();
            }
        });
        
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        add(nameTag, c);
        c.gridy = 1;
        c.weightx = 1.0;
        add(controller, c);
        c.weightx = 0.0;
        c.gridx = 2;
        add(feedback, c);
        c.gridx = 3;
        add(zeroButton, c);
        c.gridx = 4;
        
    }
    
    /**
     * Resets the slider value to zero and tells the robot
     * to turn the motor off.
     */
    public void reset() {
        controller.setBindableValue(0.0);
        valueEntry.setBindableValue(0.0);
    }

    public void propertyChanged(Property property) { }
    
}