package frc.robot.states;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.util.Constants;
import frc.robot.util.Limelight;
import frc.robot.util.geometry.Pos2;
import frc.robot.util.geometry.Twist2;
import frc.robot.util.geometry.Vector2;

public class DriveState {
    private final double WIDTH = 0.6223;

    private Vector2 frontLeft = new Vector2();
    private double oldAngle = 0;
    private double angle = 0; // Angle of front of DT
    private double oldLeftPos = 0;
    private double leftPos = 0;
    private double oldRightPos = 0;
    private double rightPos = 0;

    // Called periodically
    public void updateAngle(double angle) {
        angle = angle * Math.PI / 180 + Math.PI / 2;
        oldAngle = this.angle;
        this.angle = angle;
    }

    public void updateWheelPosition(double leftPos, double rightPos) {
        oldLeftPos = this.leftPos;
        oldRightPos = this.rightPos;
        this.leftPos = leftPos * 0.00011688933;
        this.rightPos = rightPos * 0.00011688933;
    }

    private int consecutiveOutliers = 0;
    private Vector2 lastOutlier;
    public void update(Limelight limelight) {
        // Recalculate position with encoders and gyro
        double d = (leftPos + rightPos - oldLeftPos - oldRightPos) / 2; // Average change in wheel position
        double dtheta = angle - oldAngle;
        Twist2 motion;
        if(Math.abs(dtheta) < 0.1) {
            motion = new Twist2(d, 0, dtheta);
        } else {
            motion = new Twist2(d, 0, 0);
        }
        Vector2 encoderGyroEstimate = frontLeft.add(motion.toPos2().getTranslation());

        // Recalculate position with vision
        Vector2 visionEstimate = null;
        Vector2 estimate = limelight.getPosition().sub(new Vector2(WIDTH / 2, angle, true));
        if(estimate.sub(frontLeft).norm() < Constants.minPositionChange || consecutiveOutliers > Constants.consecutiveOutliers) {
            visionEstimate = estimate;
            consecutiveOutliers = 0;
        } else if(consecutiveOutliers == 0 || lastOutlier.sub(estimate).norm() < Constants.minPositionChange) {
            // Oh no, the limelight is acting up
            consecutiveOutliers++;
        }

        // Combine estimates
        if(visionEstimate == null) {
            frontLeft = encoderGyroEstimate;
        } else {
            frontLeft = visionEstimate;
        }
    }

    public void reset() {
        frontLeft = new Vector2(0, 0);
        leftPos = 0;
        oldLeftPos = 0;
        rightPos = 0;
        oldRightPos = 0;
    }

    // Position vector of front left corner
    public Vector2 getFrontLeft() {
        return frontLeft;
    }

    public Vector2 getFrontCenter() {
        return frontLeft.add(new Vector2(WIDTH / 2, angle, true));
    }

    // Position vector of front right corner
    public Vector2 getFrontRight() {
        return frontLeft.add(new Vector2(WIDTH, angle, true));
    }

    public Vector2 getFrontLeftVelocity() {return new Vector2();}
}
