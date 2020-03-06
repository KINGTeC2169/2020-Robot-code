package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.commands.DriveCommand;
import frc.robot.states.DriveState;
import frc.robot.states.RobotState;
import frc.util.*;
import frc.util.drivers.*;

public class Drive implements Subsystem {
    private static Drive instance;
    protected static Drive getInstance(DriveCommand dCommand) {
        if(!Constants.driveEnabled) {
            return null;
        } else if(instance == null) {
            return instance = new Drive(dCommand);
        } else {
            return instance;
        }
    }

    private final DriveCommand dCommand;
    private final Limelight limelight;
    private final DriveState driveState;
//    private final AHRS navX;
    private final MiniPID visionDrive;
    private final PD turnControl;
    private final PD driveControl;
    private final PD turnTowardsZero;
    private final TalonSRX left;
    private final TalonSRX right;
    private final DSolenoid dog;

    private boolean highGear = false;
    private double oldWheel = 0;
    private double quickStopAccumulator = 0.0;
    private double negInertiaAccumulator = 0;

    private boolean linearDriveStarted = false;
    private double linearDriveDistance;
    private double linearDriveTargetAngle;
    private double linearDriveTargetDistance;
    private double linearDriveMultiplier;

    private boolean findTargetStarted = false;
    private boolean findTargetPassedZero;
    private boolean findTargetTurned360;
    private double findTargetStartingAngle;

    private Drive(DriveCommand dCommand) {
        this.dCommand = dCommand;
        limelight = Limelight.getInstance();
        driveState = RobotState.getInstance().getDriveState();

        dog = new DSolenoid(ActuatorMap.dogExtend, ActuatorMap.dogRetract);
        dog.setName("High Gear");

        left = ControllerFactory.masterTalon(ActuatorMap.leftTop, true);
        left.configOpenloopRamp(.3);
        right = ControllerFactory.masterTalon(ActuatorMap.rightTop, false);
        left.configContinuousCurrentLimit(40);
        left.configPeakCurrentLimit(70);
        left.enableCurrentLimit(true);
        left.configPeakCurrentDuration(100);
        right.configPeakCurrentLimit(70);
        right.configPeakCurrentDuration(100);
        right.configContinuousCurrentLimit(40);
        right.enableCurrentLimit(true);
        right.configOpenloopRamp(.3);
        ControllerFactory.slaveVictor(ActuatorMap.leftFront, false, left);
        ControllerFactory.slaveVictor(ActuatorMap.leftBack, false, left);
        ControllerFactory.slaveVictor(ActuatorMap.rightFront, true, right);
        ControllerFactory.slaveVictor(ActuatorMap.rightBack, true, right);

        left.setSensorPhase(false);
        right.setSensorPhase(true);

//        navX = new AHRS(SPI.Port.kMXP, (byte) 200);

        visionDrive = new MiniPID(Constants.visionDriveP, Constants.visionDriveI, Constants.visionDriveD);
        visionDrive.setSetpoint(0);
        turnControl = new PD(Constants.linearDriveTurnP, Constants.linearDriveTurnD);
        driveControl = new PD(Constants.linearDriveDriveP, Constants.linearDriveDriveD);
        turnTowardsZero = new PD(Constants.alignToGyroP, Constants.alignToGyroD);

        visionDrive.setP(Constants.visionDriveP);
        visionDrive.setI(Constants.visionDriveI);
        visionDrive.setD(Constants.visionDriveD);
        visionDrive.setMaxIOutput(.225);

//        SmartDashboard.putNumber("Vision P", Constants.visionDriveP);
//        SmartDashboard.putNumber("Vision I", Constants.visionDriveI);
//        SmartDashboard.putNumber("Vision D", Constants.visionDriveD);
    }

    @Override
    public void update() {
        dog.set(dCommand.isHighGear());

        if(dCommand.isLinearDrive()) {
            if(!linearDriveStarted) startLinearDrive(dCommand.getLinearDriveDistance(), dCommand.getLinearDriveAngle(), dCommand.getLinearDriveMultiplier());
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

        if(dCommand.isDriveForward()) {
            setOutput(1, 1);
        } else if(dCommand.isVision()) {
            visionDrive(dCommand.getThrottle());
        } else if(dCommand.isCheesy()) {
            cheesyDrive(dCommand.getThrottle(), dCommand.getWheel(), dCommand.getQuickTurn());
        } else if(dCommand.isRotate()) {
            rotateDrive(dCommand.getThrottle(), dCommand.getRotateDriveError());
        } else if(dCommand.isResting()) {
            setOutput(0, 0);
        }

        driveState.updateAngle(getAngle());
        driveState.updateWheelPosition(getLeftRotations(), getRightRotations());
    }

    @Override
    public void reset() {
        dog.set(false);
        highGear = false;
    }

    private void startLinearDrive(double distance, double angle, double multiplier) {
        linearDriveStarted = true;
        linearDriveDistance = 0;
        linearDriveTargetAngle = angle;
        linearDriveTargetDistance = distance;
        linearDriveMultiplier = multiplier;
    }

    // Drive straight with gyro assistance
    private void updateLinearDrive() {
//        double rotations = (getLeftRotations() + getRightRotations()) / 2;
//        linearDriveDistance = Conversion.rotationsToInches(rotations, Constants.driveWheelDiameter);
//        double angleController = turnControl.getOutput(linearDriveMultiplier * (navX.getAngle() - linearDriveTargetAngle));
//        double driveController = driveControl.getOutput(linearDriveTargetDistance - linearDriveDistance);
//        setOutput(driveController + angleController, driveController - angleController);
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
            if(findTargetPassedZero && findTargetStartingAngle > 0 == getAngle() > 0 && getAngle() > 0 == findTargetStartingAngle > getAngle()) findTargetTurned360 = true;
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

        if (Math.abs(quickTurn) > Constants.quickTurnDeadband) {
            if (Math.abs(throttle) < Constants.quickStopDeadband) {
                double alpha = Constants.quickStopWeight;
                quickStopAccumulator = (1 - alpha) * quickStopAccumulator + alpha * Math.min(1, Math.min(-quickTurn, -1)) * Constants.quickStopScalar;
            }
            overPower = 1;
            angularPower = -quickTurn;
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

        left.set(ControlMode.PercentOutput, leftPwm);
        right.set(ControlMode.PercentOutput, rightPwm);
    }

    // Aim at the target
    private void visionDrive(double throttle) {
        if(limelight.isValidTarget()) {
//            DriverStation.reportWarning(""+SmartDashboard.getNumber("Vision P", Constants.visionDriveP),false);
//            visionDrive.setP(SmartDashboard.getNumber("Vision P", Constants.visionDriveP));
//            visionDrive.setI(SmartDashboard.getNumber("Vision I", Constants.visionDriveI));
//            visionDrive.setD(SmartDashboard.getNumber("Vision D", Constants.visionDriveD));
//            visionDrive.setMaxIOutput(.225);
            double error = limelight.getCenter().x;
            double output = Constants.outsideP * -error;
            if(Math.abs(error) < 4) {
//                SmartDashboard.putString("In PID?", "PID");
                output = visionDrive.getOutput(error);
                left.set(ControlMode.PercentOutput, throttle - output);
                right.set(ControlMode.PercentOutput, throttle + output);
//                SmartDashboard.putNumber("PID Output", output);
            }
            else if(Math.abs(error) < 7 && Math.abs(error) > 2){
                double val = .2;
                if (error < 0){
//                    SmartDashboard.putString("In PID?", "1");
                    left.set(ControlMode.PercentOutput, -val);
                    right.set(ControlMode.PercentOutput, val);
                }
                else if (error > 0){
//                    SmartDashboard.putString("In PID?", "2");
                    left.set(ControlMode.PercentOutput, val);
                    right.set(ControlMode.PercentOutput, -val);
                }
            }
            else if (error < 0){
//                SmartDashboard.putString("In PID?", "3");
                left.set(ControlMode.PercentOutput, -.25);
                right.set(ControlMode.PercentOutput, .25);
            }
            else if (error > 0){
//                SmartDashboard.putString("In PID?", "4");
                left.set(ControlMode.PercentOutput, .25);
                right.set(ControlMode.PercentOutput, -.25);
            }

            SmartDashboard.putNumber("Error", error);
        } else {
            visionDrive.reset();
            setOutput(0, 0);
        }
    }

    // Rotates the robot to drive some error to zero
    private void rotateDrive(double throttle, double error) {
        double output = visionDrive.getOutput(error);
        left.set(ControlMode.PercentOutput, throttle - output);
        right.set(ControlMode.PercentOutput, throttle + output);
    }

    private void setOutput(double l, double r) {
        left.set(ControlMode.PercentOutput, l);
        right.set(ControlMode.PercentOutput, r);
    }

    private double getLeftRotations() {
        return Conversion.encoderTicksToRotations(left.getSelectedSensorPosition(0));
    }

    private double getRightRotations() {
        return Conversion.encoderTicksToRotations(right.getSelectedSensorPosition(0));
    }

    private double getAngle() {
//        if(Constants.usingTestBed) {
//            return Debug.getNumber("navX angle");
//        } else {
//            return navX.getAngle();
//        }
        return 10;
    }

    protected double getLinearDriveDistance() {
        return linearDriveDistance;
    }

    protected double getDriveDistance() {
        double rotations = (getLeftRotations() + getRightRotations()) / 2;
        return Conversion.rotationsToInches(rotations, Constants.driveWheelDiameter);
    }
}
