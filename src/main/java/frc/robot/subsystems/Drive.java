package frc.robot.subsystems;

import frc.robot.commands.DriveCommand;
import frc.robot.states.DriveState;
import frc.robot.states.RobotState;
import frc.util.*;
import frc.util.drivers.*;

public class Drive implements Subsystem {
    private static Drive instance;
    protected static Drive getInstance(DriveCommand dCommand) {
        if(instance == null) {
            return instance = new Drive(dCommand);
        } else {
            return instance;
        }
    }

    private final DriveCommand dCommand;
    private final Limelight limelight;
    private final DriveState driveState;
    private final NavX navX;
    private final PD visionDrive;
    private final PD turnControl;
    private final PD driveControl;
    private final PD turnTowardsZero;
    private final Talon left;
    private final Talon right;
    private final DSolenoid dog;

    private boolean highGear = false;
    private double oldWheel = 0;
    private double quickStopAccumulator = 0.0;
    private double negInertiaAccumulator = 0;

    private boolean linearDriveStarted = false;
    private double linearDriveDistance;
    private double linearDriveTargetAngle;
    private double linearDriveTargetDistance;

    private boolean findTargetStarted = false;
    private boolean findTargetPassedZero;
    private boolean findTargetTurned360;
    private double findTargetStartingAngle;

    private Drive(DriveCommand dCommand) {
        this.dCommand = dCommand;
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
        turnControl = new PD(Constants.linearDriveTurnP, Constants.linearDriveTurnD);
        driveControl = new PD(Constants.linearDriveDriveP, Constants.linearDriveDriveD);
        turnTowardsZero = new PD(Constants.alignToGyroP, Constants.alignToGyroD);
    }

    @Override
    public void update() {
        dog.set(dCommand.isHighGear());

        if(dCommand.isLinearDrive()) {
            if(!linearDriveStarted) startLinearDrive(dCommand.getLinearDriveDistance());
            updateLinearDrive();
        } else {
            linearDriveStarted = false;
        }

        if(dCommand.isFindTarget()) {
            if(!findTargetStarted) startFindTarget();
            findTarget();
        } else {
            findTargetStarted = false;
        }

        if(dCommand.isVision()) {
            visionDrive(dCommand.getThrottle());
        } else if(dCommand.isCheesy()) {
            cheesyDrive(dCommand.getThrottle(), dCommand.getWheel(), dCommand.getQuickTurn());
        } else if(dCommand.isResting()) {
            setOutput(0, 0);
        }

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

    private void startLinearDrive(double distance) {
        linearDriveStarted = true;
        linearDriveDistance = 0;
        linearDriveTargetAngle = getAngle();
        linearDriveTargetDistance = distance;
    }

    // Drive straight with gyro assistance
    private void updateLinearDrive() {
        double rotations = (getLeftRotations() + getRightRotations()) / 2;
        linearDriveDistance = Conversion.rotationsToInches(rotations, Constants.driveWheelDiameter);
        double angleController = turnControl.getOutput(linearDriveTargetAngle - navX.getAngle());
        double driveController = driveControl.getOutput(linearDriveTargetDistance - linearDriveDistance);
        setOutput(driveController + angleController, driveController - angleController);
    }

    private void startFindTarget() {
        findTargetPassedZero = false;
        findTargetTurned360 = false;
        findTargetStartingAngle = getAngle();
        findTargetStarted = true;
    }

    private void findTarget() {
        if(!findTargetTurned360) {
            // Turn towards zero angle at constant output
            if(findTargetStartingAngle > 0) {
                setOutput(Constants.turnTowardsTargetOutput, -Constants.turnTowardsTargetOutput);
            } else {
                setOutput(-Constants.turnTowardsTargetOutput, Constants.turnTowardsTargetOutput);
            }

            // Check if we've turned 360 degrees
            if(findTargetStartingAngle > 0 != getAngle() > 0) findTargetPassedZero = true;
            if(findTargetPassedZero && findTargetStartingAngle > 0 == getAngle() > 0 && getAngle() > 0 == findTargetStartingAngle < getAngle()) findTargetTurned360 = true;
        } else if(Math.abs(getAngle()) > Constants.pointingTowardsTarget) {
            // If we're not pointing towards the target, turn towards target
            double output = turnTowardsZero.getOutput(getAngle());
            setOutput(output, -output);
        } else {
            // Otherwise, drive back
            double output = turnTowardsZero.getOutput(getAngle());
            setOutput(output - Constants.backAwayFromTargetOutput, -output - Constants.backAwayFromTargetOutput);
        }
    }

    private void cheesyDrive(double throttle, double wheel, double quickTurn) {
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

        if (quickTurn != 0) {
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
    private void visionDrive(double throttle) {
        if(limelight.isValidTarget()) {
            double output = visionDrive.getOutput(limelight.getCenter().x);
            left.setOutput(throttle - output);
            right.setOutput(throttle + output);
        } else {
            setOutput(0, 0);
        }
    }

    private void setOutput(double l, double r) {
        left.setOutput(l);
        right.setOutput(r);
    }

    private double getLeftRotations() {
        return Conversion.encoderTicksToRotations(left.getSensor());
    }

    private double getRightRotations() {
        return Conversion.encoderTicksToRotations(right.getSensor());
    }

    private double getAngle() {
        if(Constants.usingTestBed) {
            return Debug.getNumber("navX angle");
        } else {
            return navX.getAngle();
        }
    }

    protected double getLinearDriveDistance() {
        return linearDriveDistance;
    }
}
