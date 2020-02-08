package frc.robot.subsystems;

import frc.robot.states.RobotState;
import frc.util.drivers.ColorSensor;
import frc.util.Debug;
import frc.util.drivers.Limelight;

public class Superstructure {

    private static Superstructure instance;

    public static Superstructure getInstance() {
        if(instance == null) {
            return instance = new Superstructure();
        } else {
            return instance;
        }
    }

    private RobotState robotState;

    // Subsystems
    private Drive drive = Drive.getInstance();
    private Patrick patrick = Patrick.getInstance();
    private Indexer indexer = Indexer.getInstance();
    private Intake intake = Intake.getInstance();
    private Shooter shooter = Shooter.getInstance();
    private Telescope telescope = Telescope.getInstance();

    // Other systems
    private Limelight limelight = Limelight.getInstance();
    private ColorSensor colorSensor = ColorSensor.getInstance();

    public void start() {
        drive.reset();
        limelight.start();
    }

    public void update(RobotState state) {
        colorSensor.update();

        // Update subsystems
        drive.update();
        intake.update();
        indexer.setFeeder(state.getIntakeState().wasRunning());
        indexer.update();
        patrick.update();
        shooter.update();
        telescope.update();

        Debug.debugAll();
    }

    public void reset() {
        drive.reset();
        intake.reset();
        indexer.reset();
        shooter.reset();
        patrick.reset();
        telescope.reset();
    }
}
