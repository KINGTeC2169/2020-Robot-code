package frc.robot.auto.modes;

import frc.robot.auto.actions.*;
import frc.robot.commands.CommandMachine;
import frc.robot.subsystems.Superstructure;

public class TestMode implements Mode {

    private final Superstructure superstructure;
    private final SearchTarget find;
    private final AimAtTarget aim;
    private final ShootBalls shoot;
    private final LinearDrive drive;
    private final RunFlywheel flywheel;
    private final ChaseMidpoint chase;
    private final Parallel parallel;
    private final Series series;

    private boolean running = false;

    public TestMode(Superstructure superstructure, CommandMachine commandMachine) {
        this.superstructure = superstructure;
        find = new SearchTarget(commandMachine.getDriveCommand());
        aim = new AimAtTarget(commandMachine.getDriveCommand());
        shoot = new ShootBalls(superstructure, commandMachine.getDriveCommand(), commandMachine.getIndexerCommand(), commandMachine.getShooterCommand());
        drive = new LinearDrive(superstructure, commandMachine.getDriveCommand(), 45, -48);
        flywheel = new RunFlywheel(commandMachine.getShooterCommand());
        chase = new ChaseMidpoint(commandMachine.getDriveCommand(), commandMachine.getIntakeCommand(), 36, 45);
        parallel = new Parallel(true, flywheel, drive);
        series = new Series(drive, flywheel);
    }

    @Override
    public void start() {
        series.start();
        running = true;
    }

    @Override
    public void run() {
        series.run();
    }

    @Override
    public void stop() {
        series.stop();
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}
