package frc.robot.auto.actions;

// Ball jail. Ball jail.

import frc.robot.commands.CommandMachine;
import frc.robot.commands.IntakeCommand;

public class BallJail implements Action {
    private final IntakeCommand iCommand;

    public BallJail() {
        iCommand = CommandMachine.getInstance().getIntakeCommand();
    }

    @Override
    public void start() {
        iCommand.setBallJail(true);
    }

    @Override
    public void run() {

    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isFinished() {
        return true;
    }
}
