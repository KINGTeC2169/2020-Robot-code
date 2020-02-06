package frc.robot.subsystems;

import frc.robot.states.DriveState;
import frc.robot.states.RobotState;
import frc.util.*;
import frc.util.drivers.*;

public class Drive implements Subsystem {
    private static Drive instance;
    public static Drive getInstance() {
        if(instance == null) {
            return instance = new Drive();
        } else {
            return instance;
        }
    }

    private Controls controls;
    private Limelight limelight;
    private DriveState driveState;
    private NavX navX;
    private PD visionDrive;

    // Master talons
    private Talon left;
    private Talon right;

    private DSolenoid dog;

    private boolean highGear = false;
    private double oldWheel = 0;
    private double quickStopAccumulator = 0.0;
    private double negInertiaAccumulator = 0;

    public Drive() {
        controls = Controls.getInstance();
        limelight = Limelight.getInstance();
        driveState = RobotState.getInstance().getDriveState();

        dog = new DSolenoid(ActuatorMap.dog);
        dog.setName("High Gear");

        left = ControllerFactory.masterTalon(ActuatorMap.leftFront, false);
        right = ControllerFactory.masterTalon(ActuatorMap.rightFront, false);
        ControllerFactory.slaveTalon(ActuatorMap.leftTop, true, left);
        ControllerFactory.slaveTalon(ActuatorMap.leftBack, true, left);
        ControllerFactory.slaveTalon(ActuatorMap.rightTop, false, right);
        ControllerFactory.slaveTalon(ActuatorMap.rightBack, false, right);

        left.setName("Left Drive");
        right.setName("Right Drive");

        navX = NavX.getInstance();

        visionDrive = new PD(Constants.visionDriveP, Constants.visionDriveD);
    }

    @Override
    public void update() {
        handleShifter();
        cheesyDrive();

        driveState.updateAngle(getAngle());
        driveState.updateWheelPosition(getLeftRotations(), getRightRotations());
    }

    @Override
    public void reset() {
        left.reset();
        right.reset();
        dog.set(false);
        highGear = false;
    }

    public void handleShifter() {
        if(controls.left.getRawButton(1)) {
            highGear = true;
        } else if(controls.left.getRawButton(2)) {
            highGear = false;
        }
        dog.set(highGear);
    }

    private double handleDeadband(double val, double deadband) {
        return (Math.abs(val) > Math.abs(deadband)) ? val : 0.0;
    }

    public void cheesyDrive() {
        double throttle = handleDeadband(controls.left.getY(), Constants.throttleDeadband);
        double wheel = handleDeadband(controls.right.getX(), Constants.wheelDeadband);
        boolean quickTurn = controls.right.getZ() > Constants.quickTurnThreshold;

        double negInertia = wheel - oldWheel;
        oldWheel = wheel;

        double wheelNonLinearity = highGear ? Constants.highWheelNonLinearity : Constants.lowWheelNonLinearity;
        final double denominator = Math.sin(Math.PI / 2 * wheelNonLinearity);
        wheel = Math.sin(Math.PI / 2.0 * wheelNonLinearity * wheel) / denominator;
        wheel = Math.sin(Math.PI / 2.0 * wheelNonLinearity * wheel) / denominator;
        if(!highGear) {
            wheel = Math.sin(Math.PI / 2.0 * wheelNonLinearity * wheel) / denominator;
        }

        double leftPwm, rightPwm, overPower;
        double sensitivity;
        double angularPower;
        double negInertiaScalar;
        if (highGear) {
            negInertiaScalar = Constants.highNegInertiaScalar;
            sensitivity = Constants.highSensitivity;
        } else {
            if (wheel * negInertia > 0) {
                // If we are moving away from 0.0
                negInertiaScalar = Constants.lowNegInertiaTurnScalar;
            } else {
                // Otherwise, we are attempting to go back to 0.0
                if (Math.abs(wheel) > Constants.lowNegInertiaThreshold) {
                    negInertiaScalar = Constants.lowNegInertiaFarScalar;
                } else {
                    negInertiaScalar = Constants.lowNegInertiaCloseScalar;
                }
            }
            sensitivity = Constants.lowSensitivity;
        }
        double negInertiaPower = negInertia * negInertiaScalar;
        negInertiaAccumulator += negInertiaPower;

        wheel += negInertiaAccumulator;
        if (negInertiaAccumulator > 1) {
            negInertiaAccumulator--;
        } else if (negInertiaAccumulator < -1) {
            negInertiaAccumulator++;
        } else {
            negInertiaAccumulator = 0;
        }

        if (quickTurn) {
            if (Math.abs(throttle) < Constants.quickStopDeadband) {
                double alpha = Constants.quickStopWeight;
                quickStopAccumulator = (1 - alpha) * quickStopAccumulator + alpha * Math.min(1, Math.min(wheel, -1)) * Constants.quickStopScalar;
            }
            overPower = 1;
            angularPower = wheel;
        } else {
            overPower = 0;
            angularPower = Math.abs(throttle) * wheel * sensitivity - quickStopAccumulator;
            if (quickStopAccumulator > 1) {
                quickStopAccumulator --;
            } else if (quickStopAccumulator < -1) {
                quickStopAccumulator ++;
            } else {
                quickStopAccumulator = 0;
            }
        }

        rightPwm = leftPwm = throttle;
        leftPwm += angularPower;
        rightPwm -= angularPower;

        if (leftPwm > 1) {
            rightPwm -= overPower * (leftPwm - 1);
            leftPwm = 1;
        } else if (rightPwm > 1) {
            leftPwm -= overPower * (rightPwm - 1);
            rightPwm = 1;
        } else if (leftPwm < -1) {
            rightPwm += overPower * (-1 - leftPwm);
            leftPwm = -1;
        } else if (rightPwm < -1) {
            leftPwm += overPower * (-1 - rightPwm);
            rightPwm = -1;
        }

        left.setOutput(leftPwm);
        right.setOutput(rightPwm);
    }

    // Aim at the target
    public void visionDrive() {
        if(limelight.isValidTarget()) {
            double output = visionDrive.getOutput(limelight.getCenter().x);
            left.setOutput(controls.left.getY() - output);
            right.setOutput(controls.left.getY() + output);
        } else {
            setOutput(0, 0);
        }
    }

    public void setOutput(double l, double r) {
        left.setOutput(l);
        right.setOutput(r);
    }

    public double getLeftRotations() {
        return Conversion.encoderTicksToRotations(left.getSensor());
    }

    public double getRightRotations() {
        return Conversion.encoderTicksToRotations(right.getSensor());
    }

    public double getAngle() {
        if(Constants.usingTestBed) {
            return Debug.getNumber("navX angle");
        } else {
            return navX.getAngle();
        }
    }
}
