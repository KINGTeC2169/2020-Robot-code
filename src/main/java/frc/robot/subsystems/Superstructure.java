package frc.robot.subsystems;

import edu.wpi.first.wpilibj.Compressor;
import frc.robot.commands.CommandMachine;
import frc.robot.states.RobotState;
import frc.util.ActuatorMap;
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
        intake = Intake.getInstance(commandMachine.getIntakeCommand());
        patrick = Patrick.getInstance(commandMachine.getPatrickCommand());
        shooter = Shooter.getInstance(commandMachine.getShooterCommand());
        telescope = Telescope.getInstance(commandMachine.getTelescopeCommand());
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

    private Compressor compressor;

    public void start() {
        reset();
        limelight.start();
        if(!Constants.usingTestBed) {
            compressor = new Compressor(ActuatorMap.pcm);
            compressor.start();
        }
    }

    public void update(RobotState state) {
        colorSensor.update();

        // Update subsystems
        if(drive != null) drive.update();

        if(indexer != null) {
            if(shooter != null) indexer.setSlowFlywheel(shooter.getRpm() < Constants.minShootingRpm);
            indexer.update();
        }

        if(intake != null) intake.update();

        if(patrick != null) patrick.update();

        if(shooter != null) {
            if(indexer != null) shooter.forceShoot(indexer.isShooting());
            shooter.update();
        }

        if(telescope != null) telescope.update();

        Debug.debugAll();
    }

    public void reset() {
        if(drive != null) drive.reset();
        if(indexer != null) indexer.reset();
        if(intake != null) intake.reset();
        if(patrick != null) patrick.reset();
        if(shooter != null) shooter.reset();
        if(telescope != null) telescope.reset();
    }

    /* For communicating */

    public double getLinearDriveDistance() {
        if(drive == null) {
            return 0;
        } else {
            return drive.getLinearDriveDistance();
        }
    }

    public double getDriveDistance() {
        if(drive == null) {
            return 0;
        } else {
            return drive.getDriveDistance();
        }
    }

    public double getBallsInFeeder() {
        if(indexer == null) {
            return 0;
        } else {
            return indexer.getBallsInFeeder();
        }
    }

    public boolean isHoodAimed() {
        if(shooter == null) {
            return false;
        } else {
            return shooter.isHoodAimed();
        }
    }
}
