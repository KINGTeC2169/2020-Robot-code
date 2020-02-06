package frc.robot.auto.actions;

import frc.robot.states.DriveState;
import frc.robot.states.RobotState;
import frc.robot.subsystems.Drive;
import frc.util.Constants;
import frc.util.Debug;
import frc.util.drivers.Limelight;
import frc.util.PD;
import frc.util.geometry.Vector2;

public class GetInRange implements Action {
    private LookAtTarget lookAtTarget;

    private DriveState state;
    private Drive drive;
    private Limelight limelight;

    private double shootingMinY;
    private double shootingMaxY;
    private double shootingMaxSlope;

    private PD alignToTarget;
    private PD alignToAngle;
    private boolean correctingWideAngle = false;
    private boolean isFinished = false;

    private double corner1;
    private double corner2;

    public GetInRange(double shootingMinY, double shootingMaxY, double shootingMaxSlope) {
        this.shootingMinY = shootingMinY;
        this.shootingMaxY = shootingMaxY;
        this.shootingMaxSlope = shootingMaxSlope;
    }

    // Too close to make a shot
    private void tooClose() {
        double output = alignToTarget.getOutput(limelight.getCenter().x);
        drive.setOutput(-Constants.tooCloseOutput - output, -Constants.tooCloseOutput + output);
    }

    // Too far to make a shot
    private void tooFar() {
        double output = alignToTarget.getOutput(limelight.getCenter().x);
        drive.setOutput(Constants.tooFarOutput - output, Constants.tooFarOutput + output);
    }

    // Angle is too wide to make a shot
    private void tooWide() {

        if(Constants.encoderPositionPrediction) {
            correctingWideAngle = true;
            double targetAngle = Constants.tooWideSteerAngle;
            if(state.getPos().translation.x > 0) targetAngle *= -1; // Turn opposite direction if on right side of power port
            double output = alignToAngle.getOutput(drive.getAngle() - targetAngle);
            drive.setOutput(-output - Constants.tooWideBackup, output - Constants.tooWideBackup);
        } else {
            // We can't do anything if we lose sight of the target, so our only option is to back up
            drive.setOutput(-Constants.tooWideBackup, -Constants.tooWideBackup);
        }
    }

    private void beginLookAtTarget() {
        if(lookAtTarget == null) {
            lookAtTarget = new LookAtTarget();
            lookAtTarget.start();
        }
        lookAtTarget.run();
    }

    @Override
    public void start() {
        state = RobotState.getInstance().getDriveState();
        drive = Drive.getInstance();
        limelight = Limelight.getInstance();
        alignToTarget = new PD(Constants.turnTowardsTargetP, Constants.turnTowardsTargetD);
        alignToAngle = new PD(Constants.alignToGyroP, Constants.alignToGyroD);
    }

    @Override
    public void run() {
        //TODO find what is too far to the side and move tolerance to constants
        final double xTolerance = 27;
        //temporary solution to get code to run
        final boolean wantToRun = true;

        Vector2[] corners = limelight.getCorners();
        if(corners.length >= 2) {
            corner1 = corners[0].x;
            corner2 = corners[1].x;
        }

        if(correctingWideAngle || limelight.isValidTarget()) {
            // Clear look at target action
            if(lookAtTarget != null) {
                lookAtTarget.stop();
                lookAtTarget = null;
            }

            Vector2 position = state.getPos().translation;
            if(Math.abs(position.x / position.y) > shootingMaxSlope && !wantToRun) {
                tooWide();
            } else {
                //TODO figure out which side belongs with what combination of the 0 and 1 points
                if(correctingWideAngle && !limelight.isValidTarget()) {
                    beginLookAtTarget();
                } else if(limelight.getDistance() < shootingMinY) {
                    tooClose();
                } else if(limelight.getDistance() > shootingMaxY) {
                    tooFar();
                } else if (Math.abs(corner1) - Math.abs(corner2) < xTolerance) {
                    Debug.putString("Side:", "1");
                } else if (Math.abs(corner2) - Math.abs(corner1) < xTolerance){
                    Debug.putString("Side:", "2");
                } else {
                    isFinished = true;
                }
                correctingWideAngle = false;
            }
        } else {
            beginLookAtTarget();
        }
    }

    @Override
    public void stop() {
        drive.setOutput(0, 0);
    }

    @Override
    public boolean isFinished() {
        return isFinished;
    }
}
