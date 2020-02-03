package frc.robot.auto.actions;

import frc.robot.subsystems.Drive;
import frc.util.Constants;
import frc.util.Limelight;
import frc.util.PD;

public class AimAtTarget implements Action {
    private LookAtTarget lookAtTarget;

    private Drive drive;
    private Limelight limelight;

    private PD alignToTarget;

    public AimAtTarget() {
    }

    private void lookAtTarget() {
        if(lookAtTarget == null) {
            lookAtTarget = new LookAtTarget();
            lookAtTarget.start();
        }
        lookAtTarget.run();
    }

    @Override
    public void start() {
        drive = Drive.getInstance();
        limelight = Limelight.getInstance();
        alignToTarget = new PD(Constants.turnTowardsTargetP, Constants.turnTowardsTargetD);
    }

    @Override
    public void run() {
        if(limelight.isValidTarget()) {
            double output = alignToTarget.getOutput(limelight.getCenter().x);
            drive.setOutput(-output, output);
        } else {
            lookAtTarget();
        }
    }

    @Override
    public void stop() {
        drive.setOutput(0, 0);
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}
