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
    private double visionDrive = 0;
    private double visionTurn = 0;

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
        // Recalculate position
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

        // Vision Targets
        final double STEER = .01;
        final double DRIVE = .26;
        final double DESIRED_TARGET_AREA = 13;
        final double MAX_DRIVE = .7;

        NetworkTable limelight = NetworkTableInstance.getDefault().getTable("limelight");
        double tv = limelight.getEntry("tv").getDouble(0),
                tx = limelight.getEntry("tx").getDouble(0),
                ty = limelight.getEntry("ty").getDouble(0),
                ta = limelight.getEntry("ta").getDouble(0);

        double[] tcornx = limelight.getEntry("tcornx").getDoubleArray(new double[0]);
        double[] tcorny = limelight.getEntry("tcorny").getDoubleArray(new double[0]);

        SmartDashboard.putNumber("ta0", limelight.getEntry("ta1").getDouble(0));
        SmartDashboard.putNumber("tcorny", tcorny[0]);

        SmartDashboard.putNumber("tx", tx);
        SmartDashboard.putNumber("tv", tv);
        SmartDashboard.putNumber("tv", tv);

        invalidTarget = tv < 1;
        if(invalidTarget) {
            visionDrive = 0;
            visionTurn = 0;
        } else {
            visionDrive = (DESIRED_TARGET_AREA - ta) * DRIVE;
            visionDrive = visionDrive < MAX_DRIVE ? visionDrive : MAX_DRIVE;
            visionTurn = tx * STEER;
        }
    }

    public double getVisionDrive() {
        return visionDrive;
    }

    public double getVisionTurn() {
        return visionTurn;
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
