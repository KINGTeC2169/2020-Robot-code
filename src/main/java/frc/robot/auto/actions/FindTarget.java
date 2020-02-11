package frc.robot.auto.actions;

import frc.robot.commands.DriveCommand;
import frc.util.drivers.Limelight;

/*
* This class does not aim at the target!
* It only gets the target within view of the Limelight
* */

public class FindTarget implements Action {
    private final DriveCommand dCommand;
    private Limelight limelight;

    public FindTarget(DriveCommand dCommand) {
        this.dCommand = dCommand;
    }

    @Override
    public void start() {
        limelight = Limelight.getInstance();
        dCommand.setFindTarget();
    }

    @Override
    public void run() {

    }

    @Override
    public void stop() {
        dCommand.rest();
    }

    @Override
    public boolean isFinished() {
        return limelight.isValidTarget();
    }
}
