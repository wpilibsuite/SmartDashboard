/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.smartdashboard.livewindow.elements;

import edu.wpi.first.smartdashboard.gui.elements.bindings.AbstractTableWidget;
import edu.wpi.first.smartdashboard.gui.elements.bindings.BooleanBindable;
import edu.wpi.first.smartdashboard.properties.Property;
import edu.wpi.first.smartdashboard.types.DataType;
import edu.wpi.first.smartdashboard.types.named.EncoderType;
import edu.wpi.first.smartdashboard.types.named.GearToothSensorType;
import edu.wpi.first.smartdashboard.types.named.QuadratureEncoderType;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Displays data from an Encoder. This displays 
 * @author Sam
 */
public class EncoderDisplay extends AbstractTableWidget {
    
    public static final DataType[] TYPES = {QuadratureEncoderType.get(), EncoderType.get(), GearToothSensorType.get()};
    
    private final JLabel speedTag = new JLabel("Speed");
    private final JLabel distanceTag = new JLabel("Distance");
    private final JLabel DPTTag = new JLabel("Distance per Tick");
    
    /** Displays the speed of the Encoder. */
    private final UneditableNumberField speed       = new UneditableNumberField();
    /** Displays how far the Encoder has traveled. */
    private final UneditableNumberField distance    = new UneditableNumberField();
    /** A field to set the distance traveled by the Encoder per tick. */
    private final BindableNumberField DPT           = new BindableNumberField(null);
    /** Displays whether or not the Encoder is reversed. */
    private BindableBooleanCheckBox reversed;
    /** Used to zero the distance traveled by the Encoder. */
    private JButton zero = new JButton("Zero Distance");

    @Override
    public void init() {
        
        reversed = new BindableBooleanCheckBox(BooleanBindable.NULL);
        
        nameTag = new NameTag(getFieldName());
        
        setBooleanBinding("Reversed", reversed);
        zero.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                distance.setBindableValue(0.0);
            }
        });
        
        setNumberBinding("Speed", speed);
        setNumberBinding("Distance", distance);
        setNumberBinding("Distance per Tick", DPT);
        
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.weightx = 1;
        add(nameTag, c);
        c.weightx = 0;
        add(speedTag, c);
        c.weightx = 1;
        add(distanceTag, c);
        c.weightx = 2;
        add(DPTTag, c);
        c.weightx = 2;
        c.gridx = 2;
        c.gridy = 1;
        add(speed, c);
        c.gridy = 2;
        add(distance, c);
        c.gridy = 3;
        add(DPT, c);
        c.gridy = 4;
        add(zero, c);
        c.gridx = 0;
        c.weightx = 0;
        add(new JLabel("Reversed"), c);
        c.gridx = 1;
        c.weightx = 10000;
        reversed.setAlignmentX(JLabel.LEFT_ALIGNMENT);
        add(reversed, c);
        
    }

    public void propertyChanged(Property property) {
        
    }
    
}
