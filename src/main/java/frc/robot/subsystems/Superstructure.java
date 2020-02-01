package frc.robot.subsystems;

import frc.robot.util.ColorSensor;
import frc.robot.util.Controls;
import frc.robot.util.Debug;
import frc.robot.util.Limelight;

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
    private Limelight limelight = new Limelight();
    private ColorSensor colorSensor = new ColorSensor();
    private Controls controls = new Controls();

    public void start() {
        drive.reset();

        limelight.start();
    }

    public void update() {
        Debug.debugAll();

        // Update subsystems
        drive.update();
        patrick.update();
    }

    public void reset() {
        drive.reset();
        patrick.reset();
    }
}
