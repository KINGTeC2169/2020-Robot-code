package frc.robot.states;

import frc.robot.util.geometry.Vector2;

public class DriveState {
    private final double WIDTH = 1;

    private Vector2 frontLeft = new Vector2(0, 0);
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
        this.leftPos = leftPos;
        this.rightPos = rightPos;
    }

    public void update() {
        // Recalculate position
        double dl = leftPos - oldLeftPos; // Left wheel displacement
        double dr = rightPos - oldRightPos; // Right wheel displacement
        double arc = Math.abs(angle - oldAngle);
        frontLeft = frontLeft.add(Vector2.chord(arc * dl, this.angle, angle));
    }

    // Position vector of front left corner
    public Vector2 getFrontLeft() {
        return frontLeft;
    }

    // Position vector of front right corner
    public Vector2 getFrontRight() {
        return frontLeft.add(new Vector2(WIDTH, angle, true));
    }
}
