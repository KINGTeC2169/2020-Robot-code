package frc.robot.auto.modes;

import frc.robot.auto.actions.*;
import frc.robot.commands.CommandMachine;
import frc.robot.subsystems.Superstructure;

public class TestMode implements Mode {

    private final SearchTarget find;
    private final AimAtTarget aim;
    private final ShootBalls shoot;
    private final LinearDrive drive;
    private final RunFlywheel flywheel;
    private final ChaseBall chase;
    private final Parallel parallel;
    private final Series series;

    private boolean running = false;

    public TestMode() {
        find = new SearchTarget();
        aim = new AimAtTarget();
        shoot = new ShootBalls();
        drive = new LinearDrive(45, -48);
        flywheel = new RunFlywheel();
        chase = new ChaseBall();
        parallel = new Parallel(true, flywheel, drive);
        series = new Series(drive, flywheel);
    }

    @Override
    public void start() {
        chase.start();
        running = true;
    }

    @Override
    public void run() {
        chase.run();
    }

    @Override
    public void stop() {
        chase.stop();
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}
