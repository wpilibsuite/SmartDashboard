package edu.wpi.first.smartdashboard.livewindow.elements;

import edu.wpi.first.smartdashboard.gui.elements.bindings.AbstractTableWidget;
import edu.wpi.first.smartdashboard.properties.Property;
import edu.wpi.first.smartdashboard.types.DataType;
import edu.wpi.first.smartdashboard.types.named.AccelerometerType;
import edu.wpi.first.smartdashboard.types.named.AnalogInputType;
import edu.wpi.first.smartdashboard.types.named.CounterType;
import edu.wpi.first.smartdashboard.types.named.GearToothSensorType;
import edu.wpi.first.smartdashboard.types.named.UltrasonicType;
import edu.wpi.first.wpilibj.tables.ITableListener;
import javax.swing.BoxLayout;


/**
 * Displays a single number (e.g. 10.43). This should be used
 * by potentiometers, accelerometers, and other sensors that send
 * a single number to the robot.
 * @author Sam
 */
public class SingleNumberDisplay extends AbstractTableWidget implements ITableListener {
    
	public static final DataType[] TYPES = {AnalogInputType.get(), 
                                            UltrasonicType.get(), 
                                            AccelerometerType.get(), 
                                            GearToothSensorType.get(),
											CounterType.get()};
    
    protected final String defaultText = " ---- ";
    private final UneditableNumberField display = new UneditableNumberField();
    
    /**
     * @inheritdoc
     */
    public void init() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        
        nameTag = new NameTag(getFieldName());
        add(nameTag);
        
        display.setFocusable(false);
        display.setText(defaultText);
        setNumberBinding("Value", display);
        add(display);
		revalidate();
		repaint();
    }

	@Override
	public void propertyChanged(Property property) {
	}
    
}
