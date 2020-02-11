package frc.robot.subsystems;

import frc.robot.commands.CommandMachine;
import frc.robot.states.RobotState;
import frc.util.Constants;
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

    private Superstructure() {
        CommandMachine commandMachine = CommandMachine.getInstance();
        drive = Drive.getInstance(commandMachine.getDriveCommand());
        indexer = Indexer.getInstance(commandMachine.getIndexerCommand());
        intake = Intake.getInstance();
        patrick = Patrick.getInstance();
        shooter = Shooter.getInstance(commandMachine.getShooterCommand());
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

    public double getBallsInFeeder() {
        return indexer.getBallsInFeeder();
    }

    public boolean isHoodAimed() {
        return shooter.isHoodAimed();
    }
}
