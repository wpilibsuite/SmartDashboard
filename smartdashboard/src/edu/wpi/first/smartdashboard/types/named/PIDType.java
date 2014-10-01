package edu.wpi.first.smartdashboard.types.named;

import edu.wpi.first.smartdashboard.gui.elements.PIDEditor;
import edu.wpi.first.smartdashboard.types.NamedDataType;

/**
 *
 * @author Joe Grinstead
 */
public class PIDType extends NamedDataType {

    public static final String LABEL = "PIDController";

    private PIDType() {
        super(LABEL, PIDEditor.class);
    }

    public static NamedDataType get() {
        if (NamedDataType.get(LABEL) != null) {
            return NamedDataType.get(LABEL);
        } else {
            return new PIDType();
        }
    }
}
