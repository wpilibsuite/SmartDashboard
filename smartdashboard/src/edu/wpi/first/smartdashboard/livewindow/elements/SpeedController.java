package edu.wpi.first.smartdashboard.livewindow.elements;

import edu.wpi.first.smartdashboard.gui.elements.bindings.NumberBindable;
import edu.wpi.first.smartdashboard.gui.elements.bindings.AbstractTableWidget;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import edu.wpi.first.smartdashboard.gui.elements.*;
import edu.wpi.first.smartdashboard.properties.*;
import edu.wpi.first.smartdashboard.types.*;
import edu.wpi.first.smartdashboard.types.named.*;

/**
 * Used to control SpeedControllers such as Victors.
 * @author Sam
 */
public class SpeedController extends AbstractTableWidget implements Controller {
    
    public static final DataType[] TYPES = {SpeedControllerType.get()};
    
    private NumberSlider controller;
    /** Used to turn off the speed controller */
    private final JButton zeroButton = new JButton("Zero");
    
    private final String defaultText = "0.0";
    private final UneditableNumberField feedback = new UneditableNumberField();
    private final NumberBindable valueEntry = getTableEntryBindable("Value");

    @Override
    public void init() {
        nameTag = new NameTag(getFieldName());
        
    	controller = new NumberSlider(valueEntry);
    	controller.setMin(-1.0);
    	controller.setMax(1.0);
    	controller.setBindableValue(0.0);
        controller.setSnapToTicks(false);
        controller.setMajorTickSpacing(50);
        controller.setPaintTicks(true);
        
        feedback.setText(defaultText);
        feedback.setColumns(4);
        
        setNumberBinding("Value", new NumberMultiBindable(feedback));
        
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

    public void propertyChanged(Property property) {
        
    }
    
}
