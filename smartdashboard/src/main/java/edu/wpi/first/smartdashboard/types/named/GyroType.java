package edu.wpi.first.smartdashboard.types.named;

import edu.wpi.first.smartdashboard.gui.elements.*;
import edu.wpi.first.smartdashboard.types.*;

/**
 *
 * @author Joe Grinstead
 */
public class GyroType extends NamedDataType {

    public static final String LABEL = "Gyro";

    private GyroType() {
        super(LABEL, Compass.class);
    }

    public static NamedDataType get() {
        if (NamedDataType.get(LABEL) != null) {
            return NamedDataType.get(LABEL);
        } else {
            return new GyroType();
        }
    }
}
