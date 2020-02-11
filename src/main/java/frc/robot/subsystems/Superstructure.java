package frc.robot.subsystems;

import frc.robot.commands.CommandMachine;
import frc.robot.states.RobotState;
import frc.util.Constants;
import frc.util.drivers.ColorSensor;
import frc.util.Debug;
import frc.util.drivers.Limelight;

public class Superstructure {

    private static Superstructure instance;

    public static Superstructure getInstance(CommandMachine commandMachine) {
        if(instance == null) {
            return instance = new Superstructure(commandMachine);
        } else {
            return instance;
        }
    }

    private Superstructure(CommandMachine commandMachine) {
        drive = Drive.getInstance(commandMachine.getDriveCommand());
        indexer = Indexer.getInstance();
        intake = Intake.getInstance();
        patrick = Patrick.getInstance();
        shooter = Shooter.getInstance();
        telescope = Telescope.getInstance();
    }

    // Subsystems
    private Drive drive;
    private Indexer indexer;
    private Intake intake;
    private Patrick patrick;
    private Shooter shooter;
    private Telescope telescope;

    // Other systems
    private Limelight limelight = Limelight.getInstance();
    private ColorSensor colorSensor = ColorSensor.getInstance();

    public void start() {
        drive.reset();
        indexer.reset();
        indexer.reset();
        patrick.reset();
        shooter.reset();
        telescope.reset();

        limelight.start();
    }

    public void update(RobotState state) {
        colorSensor.update();

        // Update subsystems
        drive.update();

        intake.update();

        indexer.setSlowFlywheel(shooter.getRpm() < Constants.minShootingRpm);
        indexer.setFunnel(state.getIntakeState().wasRunning());
        indexer.update();

        patrick.update();

        shooter.forceShoot(indexer.isShooting());
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

    /* For communicating */

    public double getLinearDriveDistance() {
        return drive.getLinearDriveDistance();
    }
}
