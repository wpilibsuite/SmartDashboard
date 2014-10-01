package edu.wpi.first.smartdashboard.livewindow.elements;

import edu.wpi.first.smartdashboard.types.DisplayElementRegistry;

/**
 *
 * @author Sam
 */
public class LiveWindowWidgetRegistrar {
    
    /**
     * Initializes all the widgets in the Live Window.
     */
    public static void init() {
        DisplayElementRegistry.registerWidget(LWSubsystem.class);
        DisplayElementRegistry.registerWidget(SpeedController.class);
        DisplayElementRegistry.registerWidget(RelayController.class);
        DisplayElementRegistry.registerWidget(DigitalOutputController.class);
        DisplayElementRegistry.registerWidget(SingleNumberDisplay.class);
        DisplayElementRegistry.registerWidget(DigitalInputDisplay.class);
        DisplayElementRegistry.registerWidget(GyroDisplay.class);
        DisplayElementRegistry.registerWidget(EncoderDisplay.class);
        DisplayElementRegistry.registerWidget(ServoController.class);
    }
    
}
