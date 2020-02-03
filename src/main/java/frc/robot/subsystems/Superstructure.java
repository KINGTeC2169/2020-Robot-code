package frc.robot.subsystems;

import frc.util.drivers.ColorSensor;
import frc.util.Controls;
import frc.util.Debug;
import frc.util.Limelight;

public class Superstructure {

    private static Superstructure instance;

    public static Superstructure getInstance() {
        if(instance == null) {
            return instance = new Superstructure();
        } else {
            return instance;
        }
    }

    // Subsystems
    private Drive drive = Drive.getInstance();
    private Patrick patrick = Patrick.getInstance();

    // Other systems
    private Limelight limelight = Limelight.getInstance();
    private ColorSensor colorSensor = ColorSensor.getInstance();
    private Controls controls = Controls.getInstance();

    public void start() {
        drive.reset();

        limelight.start();
    }

    public void update() {
        colorSensor.update();

        // Update subsystems
        drive.update();
        patrick.update();

        Debug.debugAll();
    }

    public void reset() {
        drive.reset();
        patrick.reset();
    }
}
