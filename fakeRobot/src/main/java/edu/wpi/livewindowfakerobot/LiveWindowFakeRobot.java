package edu.wpi.livewindowfakerobot;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author Sam
 */
public class LiveWindowFakeRobot {
    
    private static final NetworkTable liveWindow = NetworkTableInstance.getDefault().getTable("LiveWindow");
    
    private static final NetworkTable STATUS          = createTable(liveWindow, ".status", "LW Status"),
            
                                wrist           = createTable(liveWindow, "Wrist", "LW Subsystem"),
                                wPotentiometer  = createTable(wrist, "Potentiometer", "Analog Input"),
                                wVictor         = createTable(wrist, "Victor", "Speed Controller"), 

                                elevator        = createTable(liveWindow, "Elevator", "PIDSubsystem"), 
                                ePotentiometer  = createTable(elevator, "Potentiometer", "Analog Input"), 
                                eVictor         = createTable(elevator, "Victor", "Speed Controller"),
            
                                testSys         = createTable(liveWindow, "TestSystem", "LW Subsystem"), 
                                tComp           = createTable(testSys, "Compressor", "Compressor"), 
                                tGearTooth      = createTable(testSys, "Gear Tooth Sensor", "Gear Tooth"), 
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
                                tSwitch         = createTable(testSys, "Limit Switch", "Digital Input"),
            
                                canSystem       = createTable(liveWindow, "CAN Subsystem", "LW Subsystem"),
                                canJag          = createTable(canSystem, "CAN Jaguar", "CANSpeedController"),
                                canTalon        = createTable(canSystem, "CAN Talon", "CANSpeedController");
    
    public static void fakeRobotMain(String[] args) throws IOException {
        System.out.println();
        
        STATUS.getEntry("LW Enabled").setBoolean(true);
        STATUS.getEntry("Robot").setString("Testing");
        wPotentiometer.getEntry("Value").setDouble(2.6);
        ePotentiometer.getEntry("Value").setDouble(-11.6872);
        tSwitch.getEntry("Value").setString("Off");
        
        elevator.getEntry("p").setDouble(0.5);
        elevator.getEntry("i").setDouble(0.5);
        elevator.getEntry("d").setDouble(0.5);
        elevator.getEntry("f").setDouble(0.5);
        elevator.getEntry("setpoint").setDouble(0.5);
        elevator.getEntry("enabled").setBoolean(false);
        
        canJag.getEntry("Type").setString("CANJaguar");
        canTalon.getEntry("Type").setString("CANTalon");
        
        
        (new Timer()).schedule(
            new TimerTask(){
                @Override
                public void run() {
                    wPotentiometer.getEntry("Value").setDouble((Math.random()-.5) * 24);
                    ePotentiometer.getEntry("Value").setDouble((Math.random()-.5) * 24);
                    tPotentiometer.getEntry("Value").setDouble((Math.random()-.5) * 24);
                    tGyro.getEntry("Value").setDouble(Math.random() * 360);
                    tAccel.getEntry("Value").setDouble((Math.random()-.5)*8);
                    tSwitch.getEntry("Value").setString(Math.random() < 0.5 ? "On" : "Off");
                    tEncoder1.getEntry("Speed").setDouble(Math.random() * 20);
                    tEncoder1.getEntry("Distance").setDouble(Math.random() * 10);
                    tEncoder1.getEntry("Distance per Tick").setDouble(Math.random());
                    tCompass.getEntry("Value").setDouble(Math.random());
                    tUltra.getEntry("Value").setDouble((Math.random()-.5) * 200);
                    tGearTooth.getEntry("Value").setDouble((int)(Math.random() * 100));
                }}, 
            0, 500);
        
    }
    
    private static NetworkTable createTable(NetworkTable parent, String name, String type) {
        NetworkTable table = parent.getSubTable(name);
        System.out.println(table);
        table.getEntry(".type").setValue(type);
        table.getEntry("Name").setValue(name);
        return table;
    }
}
