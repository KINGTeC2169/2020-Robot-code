package frc.robot.auto.actions;

import frc.robot.commands.CommandMachine;
import frc.robot.commands.DriveCommand;
import frc.util.drivers.Limelight;

public class GetInRange implements Action {
    private SearchTarget lookAtTarget;

    private final DriveCommand dCommand;
    private final Limelight limelight;

    private final double shootingMaxD;

    public GetInRange(double shootingMaxD) {
        dCommand = CommandMachine.getInstance().getDriveCommand();
        limelight = Limelight.getInstance();
        this.shootingMaxD = shootingMaxD;
    }

    // Too far to make a shot
    private void tooFar() {
        dCommand.setAutoVision(1);
    }

    private void beginLookAtTarget() {
        if(lookAtTarget == null) {
            lookAtTarget = new SearchTarget();
            lookAtTarget.start();
        }
        lookAtTarget.run();
    }

    @Override
    public void start() {
    }

    @Override
    public void run() {
        if(limelight.isValidTarget()) {
            // Clear look at target action
            if(lookAtTarget != null) {
                lookAtTarget.stop();
                lookAtTarget = null;
            }

            if(limelight.getDistance() > shootingMaxD) {
                tooFar();
            }
        } else {
            beginLookAtTarget();
        }
    }

    @Override
    public void stop() {
        dCommand.rest();
    }

    @Override
    public boolean isFinished() {
        return limelight.isValidTarget() && limelight.getDistance() <= shootingMaxD;
    }
}
