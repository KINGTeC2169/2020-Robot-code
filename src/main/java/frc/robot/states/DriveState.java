package frc.robot.states;

import frc.util.Constants;
import frc.util.Conversion;
import frc.util.Debug;
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

        Vector2[] corners = limelight.getCorners();

        if(corners.length == 4) {
            // Calculate angles from pixel values
            double p1ty = Conversion.degToRad((25/360.0) * (359.5 - corners[0].y));
            double p2ty = Conversion.degToRad((25/360.0) * (359.5 - corners[1].y));

            // Calculate lengths between the camera and the points on the ground
            double cd1 = 66.25 / Math.tan(p1ty);
            double cd2 = 66.25 / Math.tan(p2ty);

            // Calculate angle d1d2c
            double d1d2 = 20 * Math.sqrt(3);
            double d1d2c = Math.acos((cd1*cd1 + d1d2*d1d2 - cd2*cd2) / (2*cd1*d1d2)); // Law of cosines

            // Calculate x and y
            double robotY = cd2 * Math.sin(d1d2c > Math.PI / 2 ? d1d2c : Math.PI - d1d2c);
            visionEstimate = new Vector2(
                    Math.sqrt(cd2*cd2 - robotY * robotY) * ((d1d2c > Math.PI / 2) ? 1 : -1),
                    robotY
            );

            Debug.visionEstimate(Conversion.radToDeg(p1ty), Conversion.radToDeg(p2ty), cd1, cd2, Conversion.radToDeg(d1d2c), visionEstimate);
        } else {
            visionEstimate = new Vector2();
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
