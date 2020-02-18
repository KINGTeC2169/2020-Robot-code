package frc.robot.commands;

import frc.util.Constants;
import frc.util.Controls;

public class DriveCommand {
    private static DriveCommand instance;
    protected static DriveCommand getInstance() {
        if(instance == null) {
            return instance = new DriveCommand();
        } else {
            return instance;
        }
    }

    private enum DriveCommandState {RESTING, CHEESY, AUTO_VISION, ARCADE_VISION, ROTATE_DRIVE, FIND_TARGET, LINEAR_DRIVE}

    private final Controls controls;

    private DriveCommandState state;
    private double autoVisionThrottle;
    private double rotateDriveThrottle;
    private double rotateDriveError;
    private double linearDriveDistance;
    private boolean highGear;

    private DriveCommand() {
        controls = Controls.getInstance();
    }

    private double handleDeadband(double val, double deadband) {
        return (Math.abs(val) > Math.abs(deadband)) ? val : 0.0;
    }

    protected void teleop() {
        if(controls.right.getRawButton(2)) {
            state = DriveCommandState.ARCADE_VISION;
        } else {
            state = DriveCommandState.CHEESY;
        }

        // Dog shifting
        if(controls.left.getRawButton(1)) {
            highGear = true;
        } else if(controls.left.getRawButton(2)) {
            highGear = false;
        }
    }

    /* Auto stuff */

    public void rest() {
        state = DriveCommandState.RESTING;
    }

    public void setAutoVision(double throttle) {
        state = DriveCommandState.AUTO_VISION;
        autoVisionThrottle = throttle;
    }

    public void setLinearDrive(double distance) {
        state = DriveCommandState.LINEAR_DRIVE;
        linearDriveDistance = distance;
    }

    public void setFindTarget() {
        state = DriveCommandState.FIND_TARGET;
    }

    public void setRotateDrive(double throttle, double error) {
        state = DriveCommandState.ROTATE_DRIVE;
        rotateDriveThrottle = throttle;
        rotateDriveError = error;
    }

    /* Getters */

    public double getLinearDriveDistance() {
        if(state == DriveCommandState.LINEAR_DRIVE) {
            return linearDriveDistance;
        } else {
            return 0;
        }
    }

    public double getThrottle() {
        if(state == DriveCommandState.ROTATE_DRIVE) {
            return rotateDriveThrottle;
        } else if(state == DriveCommandState.CHEESY || state == DriveCommandState.ARCADE_VISION) {
            return handleDeadband(-controls.left.getY(), Constants.throttleDeadband);
        } else if(state == DriveCommandState.AUTO_VISION) {
            return autoVisionThrottle;
        } else {
            return 0;
        }
    }

    public double getRotateDriveError() {
        if(state == DriveCommandState.ROTATE_DRIVE) {
            return rotateDriveError;
        } else {
            return 0;
        }
    }

    public double getWheel() {
        if(state == DriveCommandState.CHEESY) {
            return handleDeadband(controls.right.getX(), Constants.wheelDeadband);
        } else {
            return 0;
        }
    }

    public double getQuickTurn() {
        if(state == DriveCommandState.CHEESY) {
            return handleDeadband(controls.right.getZ(), Constants.quickTurnDeadband);
        } else {
            return 0;
        }
    }

    public boolean isResting() {
        return state == DriveCommandState.RESTING;
    }

    public boolean isCheesy() {
        return state == DriveCommandState.CHEESY;
    }

    public boolean isVision() {
        return state == DriveCommandState.ARCADE_VISION || state == DriveCommandState.AUTO_VISION;
    }

    public boolean isRotate() {
        return state == DriveCommandState.ROTATE_DRIVE;
    }

    public boolean isFindTarget() {
        return state == DriveCommandState.FIND_TARGET;
    }

    public boolean isLinearDrive() {
        return state == DriveCommandState.LINEAR_DRIVE;
    }

    public boolean isHighGear() {
        return highGear;
    }
}
