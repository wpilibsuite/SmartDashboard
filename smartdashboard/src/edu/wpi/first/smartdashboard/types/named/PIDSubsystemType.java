package edu.wpi.first.smartdashboard.types.named;

import edu.wpi.first.smartdashboard.types.NamedDataType;

/**
 *
 * @author Joe Grinstead
 */
public class PIDSubsystemType extends NamedDataType {

    public static final String LABEL = "PIDSubsystem";

    private PIDSubsystemType() {
        super(LABEL, SubsystemType.get(), PIDType.get());
    }

    public static NamedDataType get() {
        if (NamedDataType.get(LABEL) != null) {
            return NamedDataType.get(LABEL);
        } else {
            return new PIDSubsystemType();
        }
    }
}
