package frc.robot.auto.actions;

import frc.robot.commands.ShooterCommand;

public class RunFlywheel implements Action {

    private final ShooterCommand sCommand;

    public RunFlywheel(ShooterCommand sCommand) {
        this.sCommand = sCommand;
    }

    @Override
    public void start() {
        sCommand.shoot(true);
    }

    @Override
    public void run() {
        sCommand.shoot(true);
    }

    @Override
    public void stop() {
        sCommand.rest();
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}
