package frc.robot.states;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.util.geometry.Vector2;

public class DriveState {
    private final double WIDTH = 0.6223;

    private Vector2 frontLeft = new Vector2(0, 0);
    private Vector2 frontLeftVelocity = new Vector2(0, 0);
    private double oldAngle = 0;
    private double angle = 0; // Angle of front of DT
    private double oldLeftPos = 0;
    private double leftPos = 0;
    private double oldRightPos = 0;
    private double rightPos = 0;

    private boolean invalidTarget = true;
    private double tx = 0; // Target X displacement
    private double ty = 0; // Target Y displacement
    private double ta = 0; // Target area

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

    public void update() {
        // Recalculate position with encoders
        double dl = leftPos - oldLeftPos; // Left wheel displacement
        double arc = Math.abs(angle - oldAngle);
        if(arc < 0.1) {
            frontLeftVelocity = new Vector2(dl * Math.cos(angle), dl * Math.sin(angle));
        } else {
            frontLeftVelocity = Vector2.chord(dl / arc, oldAngle, angle);
        }
        frontLeft = frontLeft.add(frontLeftVelocity);

        SmartDashboard.putNumber("Front Left X", frontLeft.x);
        SmartDashboard.putNumber("Front Left Y", frontLeft.y);
        SmartDashboard.putNumber("Front Left X Vel", frontLeftVelocity.x);
        SmartDashboard.putNumber("Front Left Y Vel", frontLeftVelocity.y);

        NetworkTable limelight = NetworkTableInstance.getDefault().getTable("limelight");
        tx = limelight.getEntry("tx").getDouble(0);
        ty = limelight.getEntry("ty").getDouble(0);
        ta = limelight.getEntry("ta").getDouble(0);
        invalidTarget = limelight.getEntry("tv").getDouble(0) > 1;
    }

    public double getTx() {
        return tx;
    }

    public double getTy() {
        return ty;
    }

    public double getTa() {
        return ta;
    }

    public boolean isInvalidTarget() {
        return invalidTarget;
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

    // Position vector of front right corner
    public Vector2 getFrontRight() {
        return frontLeft.add(new Vector2(WIDTH, angle, true));
    }

    public Vector2 getFrontLeftVelocity() {return frontLeftVelocity;}
}
