package frc.robot.auto.actions;

import frc.robot.subsystems.Drive;
import frc.robot.util.Constants;
import frc.robot.util.Limelight;
import frc.robot.util.PD;

/*
* This class does not aim at the target!
* It only gets the target within view of the Limelight
* */

public class LookAtTarget implements Action {
    private Drive drive;
    private Limelight limelight;

    private PD turnTowardsZero = new PD(Constants.alignToGyroP, Constants.alignToGyroD);

    private double startingAngle;
    private boolean passedZero;
    private boolean turned360;

    @Override
    public void start() {
        drive = Drive.getInstance();
        limelight = Limelight.getInstance();
        startingAngle = drive.getAngle();
        passedZero = false;
        turned360 = false;
    }

    @Override
    public void run() {
        if(!turned360) {
            // Turn towards zero angle at constant output
            if(startingAngle > 0) {
                drive.setOutput(Constants.turnTowardsTargetOutput, -Constants.turnTowardsTargetOutput);
            } else {
                drive.setOutput(-Constants.turnTowardsTargetOutput, Constants.turnTowardsTargetOutput);
            }

            // Check if we've turned 360 degrees
            if(startingAngle > 0 != drive.getAngle() > 0) passedZero = true;
            if(passedZero && (
               startingAngle > 0 && startingAngle > drive.getAngle() ||
               startingAngle < 0 && startingAngle < drive.getAngle())
            ) turned360 = true;

        } else if(Math.abs(drive.getAngle()) > Constants.pointingTowardsTarget) {
            // If we're not pointing towards the target, turn towards target
            double output = turnTowardsZero.getOutput(drive.getAngle());
            drive.setOutput(-output, output);
        } else {
            // Otherwise, drive back
            double output = turnTowardsZero.getOutput(drive.getAngle());
            drive.setOutput(-output - Constants.backAwayFromTargetOutput, output - Constants.backAwayFromTargetOutput);
        }
    }

    @Override
    public void stop() {
        drive.setOutput(0, 0);
    }

    @Override
    public boolean isFinished() {
        return limelight.isValidTarget();
    }
}
