package frc.robot.auto.actions;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.subsystems.Drive;
import frc.util.Constants;

public class LinearDrive implements Action {
    private Drive drive;
    private double distance = 0;
    private double targetAngle;
    private double targetDistance;

    public LinearDrive(double distance) {
        drive = Drive.getInstance();
        targetDistance = distance;
    }

    @Override
    public void start() {
        targetAngle = drive.getAngle();
        drive.reset();
    }

    @Override
    public void run() {
        double newDistance = drive.getLeftRotations();
        double dDistance = distance - newDistance;
        distance = newDistance;

        double angleController = Constants.linearDriveTurnP * (targetAngle - drive.getAngle());
        double driveController =
                Constants.linearDriveDriveP * (targetDistance - distance) +
                Constants.linearDriveDriveD * (dDistance);
        SmartDashboard.putNumber("distance", distance);
        SmartDashboard.putNumber("targetDistance", targetDistance);
        SmartDashboard.putNumber("error", targetDistance - distance);
        drive.setOutput(driveController, driveController);

        if(isFinished()) stop();
    }

    @Override
    public void stop() {
        drive.setOutput(0, 0);
    }

    @Override
    public boolean isFinished() {
        return distance >= targetDistance;
    }
}
