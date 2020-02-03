package frc.robot.states;

import frc.util.Constants;
import frc.util.Conversion;
import frc.util.Interpolate;
import frc.util.drivers.Limelight;
import frc.util.geometry.*;

public class DriveState {
    private final double WIDTH = 26;
    private final double LENGTH = 26;

    private Limelight limelight;

    private Pos2 pos = new Pos2();
    private Pos2 oldPos = new Pos2();
    private Rotation2 oldRotation;
    private Rotation2 rotation;
    /* Drive wheel distance traveled in inches */
    private double oldLeft = 0;
    private double left = 0;
    private double oldRight = 0;
    private double right = 0;

    public DriveState() {
        limelight = Limelight.getInstance();
    }

    public void updateAngle(double angle) {
        oldRotation = rotation;
        rotation = new Rotation2(Conversion.degToRad(angle)).rotateC();
    }

    public void updateWheelPosition(double leftRotations, double rightRotations) {
        oldLeft = left;
        oldRight = right;
        left = Conversion.rotationsToInches(leftRotations, Constants.driveWheelDiameter);
        right = Conversion.rotationsToInches(rightRotations, Constants.driveWheelDiameter);
    }

    private int consecutiveOutliers = 0;
    private Vector2 lastOutlier;
    public void update() {
        // Recalculate position with encoders and gyro
        double d = ((left - oldLeft) + (right - oldRight)) / 2; // Average change in wheel position
        double dtheta = rotation.angle - oldRotation.angle;
        Twist2 kinematics = new Twist2(d,0, dtheta);
        Pos2 encoderGyroEstimate = pos.transform(kinematics.toPos2());

        // Recalculate position with vision
        Vector2 visionEstimate = null;
        Vector2 position = limelight.getPosition().sub(new Vector2(LENGTH / 2, pos.rotation.rotateCC().angle, true));
        if(position.sub(pos.translation).norm() < Constants.maxPositionChange || consecutiveOutliers > Constants.consecutiveOutliers) {
            visionEstimate = position;
            consecutiveOutliers = 0;
        } else if(consecutiveOutliers == 0 || lastOutlier.sub(position).norm() < Constants.maxPositionChange) {
            // Oh no, the limelight is acting up
            lastOutlier = position;
            consecutiveOutliers++;
        }

        // Combine estimates
        pos = encoderGyroEstimate;
        /*if(visionEstimate == null && Constants.encoderPositionPrediction) {
            pos.translation = encoderGyroEstimate;
        } else if(Constants.visionPositionPrediction && Constants.encoderPositionPrediction) {
            pos.translation = Interpolate.interpolate(encoderGyroEstimate, visionEstimate, 0.2);
        } else if(Constants.visionPositionPrediction) {
            pos.translation = visionEstimate;
        }*/
    }

    public void reset() {
        pos = new Pos2(new Vector2(), new Rotation2(-Math.PI / 2));
        oldPos = new Pos2(new Vector2(), new Rotation2(-Math.PI / 2));
        rotation = new Rotation2(-Math.PI / 2);
        oldRotation = new Rotation2(-Math.PI / 2);
        left = 0;
        oldLeft = 0;
        right = 0;
        oldRight = 0;
    }

    // Position vector of front left corner
    public Pos2 getPos() {
        return pos;
    }
}
