
package livewindowfakerobot;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.tables.ITable;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author Sam
 */
public class LiveWindowFakeRobot {
    
    private static final NetworkTable liveWindow = NetworkTable.getTable("LiveWindow");
    
    private static final ITable STATUS          = createTable(liveWindow, "~STATUS~", "LW Status"), 
            
                                wrist           = createTable(liveWindow, "Wrist", "LW Subsystem"),
                                wPotentiometer  = createTable(wrist, "Potentiometer", "Analog Input"),
                                wVictor         = createTable(wrist, "Victor", "Speed Controller"), 

                                elevator        = createTable(liveWindow, "Elevator", "LW Subsystem"), 
                                ePotentiometer  = createTable(elevator, "Potentiometer", "Analog Input"), 
                                eVictor         = createTable(elevator, "Victor", "Speed Controller"),
            
                                testSys         = createTable(liveWindow, "TestSystem", "LW Subsystem"), 
                                tComp           = createTable(testSys, "Compressor", "Compressor"), 
                                tGearTooth      = createTable(testSys, "Gear Tooth Sensor", "Gear Tooth Sensor"), 
                                tVictor         = createTable(testSys, "Victor", "Speed Controller"), 
                                tPotentiometer  = createTable(testSys, "Potentiometer", "Analog Input"), 
                                tRelay          = createTable(testSys, "Spike", "Relay"), 
                                tDigitalOutput  = createTable(testSys, "Digital Output", "Digital Output"), 
                                tGyro           = createTable(testSys, "Gyro", "LWGyro"), 
                                tSolenoid       = createTable(testSys, "Solenoid", "Solenoid"), 
                                tServo          = createTable(testSys, "Serov the Servo", "Servo"), 
                                tAccel          = createTable(testSys, "Accelerometer", "Accelerometer"), 
                                tEncoder1       = createTable(testSys, "Encoder 1", "Encoder"), 
                                tUltra          = createTable(testSys, "Ultrasonic", "Ultrasonic"), 
                                tCompass        = createTable(testSys, "Compass", "Compass"), 
                                tSwitch         = createTable(testSys, "Limit Switch", "Digital Input");
    
    public static void main(String[] args) {
        
        System.out.println();
        
        STATUS.putBoolean("LW Enabled", false);
        STATUS.putString("Robot", "Testing");
        wPotentiometer.putNumber("Value", 2.6);
        ePotentiometer.putNumber("Value", -11.6872);
        tSwitch.putString("Value", "Off");
        
        
        (new Timer()).schedule(
            new TimerTask(){
                @Override
                public void run() {
                    wPotentiometer.putNumber("Value", (Math.random()-.5) * 24);
                    ePotentiometer.putNumber("Value", (Math.random()-.5) * 24);
                    tPotentiometer.putNumber("Value", (Math.random()-.5) * 24);
                    tGyro.putNumber("Value", Math.random() * 360);
                    tAccel.putNumber("Value", (Math.random()-.5)*8);
                    tSwitch.putString("Value", Math.random() < 0.5 ? "On" : "Off");
                    tEncoder1.putNumber("Speed", Math.random() * 20);
                    tEncoder1.putNumber("Distance", Math.random() * 10);
                    tEncoder1.putNumber("Distance per Tick", Math.random());
                    tCompass.putNumber("Value", Math.random());
                    tUltra.putNumber("Value", (Math.random()-.5) * 200);
                    tGearTooth.putNumber("Value", (int)(Math.random() * 100));
                }}, 
            0, 500);
        
    }
    
    private static ITable createTable(ITable parent, String name, String type) {
        ITable table = parent.getSubTable(name);
        System.out.println(table);
        table.putString("~TYPE~", type);
        table.putString("Name", name);
        return table;
    }
}
