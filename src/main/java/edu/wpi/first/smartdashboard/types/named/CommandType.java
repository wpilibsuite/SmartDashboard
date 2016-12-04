package edu.wpi.first.smartdashboard.types.named;

import edu.wpi.first.smartdashboard.gui.elements.*;
import edu.wpi.first.smartdashboard.types.*;

/**
 *
 * @author Joe Grinstead
 */
public class CommandType extends NamedDataType {

    public static final String LABEL = "Command";

    private CommandType() {
        super(LABEL, Command.class);
    }

    public static NamedDataType get() {
        if (NamedDataType.get(LABEL) != null) {
            return NamedDataType.get(LABEL);
        } else {
            return new CommandType();
        }
    }
}
