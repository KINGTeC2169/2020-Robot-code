package frc.robot.auto.actions;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.subsystems.Drive;
import frc.util.Constants;
import frc.util.Conversion;
import frc.util.PD;

public class LinearDrive implements Action {
    private Drive drive;
    private PD turnControl;
    private PD driveControl;
    private double distance = 0;
    private double targetAngle;
    private double targetDistance;

    public LinearDrive(double distance) {
        turnControl = new PD(Constants.linearDriveTurnP, Constants.linearDriveTurnD);
        driveControl = new PD(Constants.linearDriveDriveP, Constants.linearDriveDriveD);
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
        distance = Conversion.rotationsToInches(drive.getLeftRotations(), Constants.driveWheelDiameter);
        double angleController = turnControl.getOutput(targetAngle - drive.getAngle());
        double driveController = driveControl.getOutput(targetDistance - distance);
        drive.setOutput(driveController + angleController, driveController - angleController);

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
