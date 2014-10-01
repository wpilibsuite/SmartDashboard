package edu.wpi.first.smartdashboard.types.named;

import edu.wpi.first.smartdashboard.gui.elements.*;
import edu.wpi.first.smartdashboard.types.*;

/**
 *
 * @author Joe Grinstead
 */
public class SubsystemType extends NamedDataType {

    public static final String LABEL = "Subsystem";

    private SubsystemType() {
        super(LABEL, Subsystem.class);
    }

    public static NamedDataType get() {
        if (NamedDataType.get(LABEL) != null) {
            return NamedDataType.get(LABEL);
        } else {
            return new SubsystemType();
        }
    }
}
