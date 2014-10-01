package edu.wpi.first.smartdashboard.types.named;

import edu.wpi.first.smartdashboard.types.NamedDataType;

/**
 *
 * @author Joe Grinstead
 */
public class PIDCommandType extends NamedDataType {

    public static final String LABEL = "PIDCommand";

    private PIDCommandType() {
        super(LABEL, CommandType.get(), PIDType.get());
    }

    public static NamedDataType get() {
        if (NamedDataType.get(LABEL) != null) {
            return NamedDataType.get(LABEL);
        } else {
            return new PIDCommandType();
        }
    }
}
