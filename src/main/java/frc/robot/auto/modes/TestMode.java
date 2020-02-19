package frc.robot.auto.modes;

import frc.robot.auto.actions.*;
import frc.robot.commands.CommandMachine;
import frc.robot.subsystems.Superstructure;

public class TestMode implements Mode {

    private final Superstructure superstructure;
    private final FindTarget find;
    private final AimAtTarget aim;
    private final ShootBalls shoot;
    private final ChaseBall chase;

    private boolean running = false;

    public TestMode(Superstructure superstructure, CommandMachine commandMachine) {
        this.superstructure = superstructure;
        find = new FindTarget(commandMachine.getDriveCommand());
        aim = new AimAtTarget(commandMachine.getDriveCommand());
        shoot = new ShootBalls(superstructure, commandMachine.getDriveCommand(), commandMachine.getIndexerCommand(), commandMachine.getShooterCommand());
        chase = new ChaseBall(commandMachine.getDriveCommand(), commandMachine.getIntakeCommand(), -90, 7);
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
