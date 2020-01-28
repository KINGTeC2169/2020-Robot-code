package frc.robot.auto.actions;

import frc.robot.subsystems.Drive;
import frc.robot.util.Constants;

public class TurnInPlace implements Action {
    private Drive drive;
    private double angle;
    private double targetAngle = 0;

    public TurnInPlace(double targetAngle) {
        drive = Drive.getInstance();
        this.targetAngle = targetAngle;
    }

    @Override
    public void start() {

    }

    @Override
    public void run() {
        double dAngle = drive.getAngle() - angle;
        angle = drive.getAngle();

        double controller =
                Constants.turnInPlaceP * (targetAngle - drive.getAngle()) +
                Constants.turnInPlaceD * dAngle;
        drive.setOutput(-controller, controller);
    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isFinished() {
        return Math.pow(targetAngle - drive.getAngle(), 2) > Constants.turnInPlaceAllowedError; // Squared error
    }
}
