package frc.robot.auto.actions;

import frc.robot.commands.CommandMachine;
import frc.robot.commands.IntakeCommand;

public class RunIntake implements Action {

    private final IntakeCommand iCommand;

    public RunIntake() {
        iCommand = CommandMachine.getInstance().getIntakeCommand();
    }

    @Override
    public void start() {
        iCommand.intake();
    }

    @Override
    public void run() {
        iCommand.intake();
    }

    @Override
    public void stop() {
        iCommand.rest();
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}
