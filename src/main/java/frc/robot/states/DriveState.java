package frc.robot.states;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.util.geometry.Vector2;

public class DriveState {
    private final double WIDTH = 0.8128;

    private Vector2 frontLeft = new Vector2(0, 0);
    private Vector2 frontLeftVelocity = new Vector2(0, 0);
    private double oldAngle = 0;
    private double angle = 0; // Angle of front of DT
    private double oldLeftPos = 0;
    private double leftPos = 0;
    private double oldRightPos = 0;
    private double rightPos = 0;

    // Called periodically
    public void updateAngle(double angle) {
        oldAngle = this.angle;
        this.angle = angle;
    }

    public void updateWheelPosition(double leftPos, double rightPos) {
        oldLeftPos = this.leftPos;
        oldRightPos = this.rightPos;
        this.leftPos = leftPos * 0.00920388;
        this.rightPos = rightPos * 0.00920388;
    }

    public void update() {
        // Recalculate position
        double dl = leftPos - oldLeftPos; // Left wheel displacement
        double arc = Math.abs(angle - oldAngle);
        frontLeftVelocity = Vector2.chord(arc * dl, this.angle, angle);
        frontLeft = frontLeft.add(frontLeftVelocity);

        SmartDashboard.putNumber("Front Left X", frontLeft.x);
        SmartDashboard.putNumber("Front Left Y", frontLeft.y);
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
