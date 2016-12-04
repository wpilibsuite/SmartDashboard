/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.smartdashboard.livewindow.elements;

import edu.wpi.first.smartdashboard.gui.elements.bindings.AbstractTableWidget;
import edu.wpi.first.smartdashboard.properties.Property;
import edu.wpi.first.smartdashboard.types.DataType;
import edu.wpi.first.smartdashboard.types.named.DoubleSolenoidType;
import edu.wpi.first.smartdashboard.types.named.RelayType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;

/**
 * Used to control relays, which give out positive, negative, or no voltage
 * ("Forward," "Off," and "Reverse") without control over how much is given
 * out. Useful for Spikes.
 * @author Sam
 */
public class RelayController extends AbstractTableWidget implements Controller {
    
    public static final DataType[] TYPES = {RelayType.get(), DoubleSolenoidType.get()};
    
    private final String[] options = {"Forward", "Off", "Reverse"};
    private final JComboBox controller = new JComboBox(options);
    
    @Override
    public void init() {
        
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        
        nameTag = new NameTag(getFieldName());
        add(nameTag);
        controller.setSelectedIndex(1);
        controller.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                table.putString("Value", controller.getSelectedItem().toString());
            }
        });
        add(controller);
        
        revalidate();
        repaint();
        
    }

    public void propertyChanged(Property property) {
        
    }

    public void reset() {
        table.putString("Value", "Off");
        controller.setSelectedIndex(1);
    }
    
    
}
