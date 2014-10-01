package edu.wpi.first.smartdashboard.gui.elements;

import edu.wpi.first.smartdashboard.types.DisplayElementRegistry;

/**
 *
 * @author Joe Grinstead
 */
public class DefaultDisplayElementRegistrar {

    public static void init() {
        DisplayElementRegistry.registerWidget(CommandButton.class);
        DisplayElementRegistry.registerWidget(BooleanBox.class);
        DisplayElementRegistry.registerWidget(Compass.class);
        DisplayElementRegistry.registerWidget(Button.class);
        DisplayElementRegistry.registerWidget(FormattedField.class);
        DisplayElementRegistry.registerWidget(LinePlot.class);
        DisplayElementRegistry.registerWidget(ProgressBar.class);
        DisplayElementRegistry.registerWidget(SimpleDial.class);
        DisplayElementRegistry.registerWidget(TextBox.class);
        DisplayElementRegistry.registerWidget(CheckBox.class);
        DisplayElementRegistry.registerWidget(PIDEditor.class);
        DisplayElementRegistry.registerWidget(Chooser.class);
        DisplayElementRegistry.registerWidget(Subsystem.class);
        DisplayElementRegistry.registerWidget(Command.class);
        DisplayElementRegistry.registerWidget(Scheduler.class);
        
        DisplayElementRegistry.registerStaticWidget(Image.class);
        DisplayElementRegistry.registerStaticWidget(ConnectionIndicator.class);
        DisplayElementRegistry.registerStaticWidget(Label.class);
        DisplayElementRegistry.registerStaticWidget(RobotPreferences.class);
        DisplayElementRegistry.registerStaticWidget(VideoStreamViewerExtension.class);
    }

}
