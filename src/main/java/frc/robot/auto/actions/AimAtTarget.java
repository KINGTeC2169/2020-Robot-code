package frc.robot.auto.actions;

import frc.robot.commands.DriveCommand;
import frc.util.Constants;
import frc.util.drivers.Limelight;

public class AimAtTarget implements Action {
    private FindTarget findTarget;

    private final DriveCommand dCommand;
    private final Limelight limelight;

    public AimAtTarget(DriveCommand dCommand) {
        this.dCommand = dCommand;
        limelight = Limelight.getInstance();
    }

    private void lookAtTarget() {
        if(findTarget == null) {
            findTarget = new FindTarget(dCommand);
            findTarget.start();
        }
        findTarget.run();
    }

    @Override
    public void start() {

    }

    @Override
    public void run() {
        if(limelight.isValidTarget()) {
            if(findTarget != null) {
                findTarget.stop();
                findTarget = null;
            }
            dCommand.setAutoVision(0);
        } else {
            lookAtTarget();
        }
    }

    @Override
    public void stop() {
        if(findTarget != null) findTarget.stop();
        dCommand.rest();
    }

    @Override
    public boolean isFinished() {
        return limelight.isValidTarget() && Math.abs(limelight.getCenter().x) < Constants.acceptedAimError;
    }
}
