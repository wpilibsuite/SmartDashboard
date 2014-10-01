/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.smartdashboard.types.named;

import edu.wpi.first.smartdashboard.livewindow.elements.SingleNumberDisplay;
import edu.wpi.first.smartdashboard.types.NamedDataType;

/**
 *
 * @author Sam
 */
public class GearToothSensorType extends NamedDataType {
    
    public static final String LABEL = "Gear Tooth";

    private GearToothSensorType() {
        super(LABEL, SingleNumberDisplay.class);
    }

    public static NamedDataType get() {
        if (NamedDataType.get(LABEL) != null) {
            return NamedDataType.get(LABEL);
        } else {
            return new GearToothSensorType();
        }
    }
    
}