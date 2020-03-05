package frc.robot.auto.actions;

import frc.robot.commands.CommandMachine;
import frc.robot.commands.DriveCommand;

public class TimedDrive implements Action {
    private final DriveCommand dCommand;
    private final int cycles;
    private int time = 0;
    private boolean finished = false;

    public TimedDrive(final double seconds) {
        dCommand = CommandMachine.getInstance().getDriveCommand();
        cycles = (int) (seconds * 50);
    }


    @Override
    public void start() {
        dCommand.driveForward();
    }

    @Override
    public void run() {
        if(finished || ++time >= cycles) {
            finished = true;
            stop();
        }
    }

    @Override
    public void stop() {
        dCommand.rest();
    }

    @Override
    public boolean isFinished() {
        return finished;
    }
}
