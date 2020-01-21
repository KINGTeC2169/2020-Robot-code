package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.states.DriveState;
import frc.robot.util.ActuatorMap;
import frc.robot.util.Controls;

class Drive{
    private Drive instance;
    private AHRS navX;

    public Drive getInstance() {
        if(instance == null) {
            return instance = new Drive();
        } else {
            return instance;
        }
    }

    private TalonSRX left = new TalonSRX(ActuatorMap.leftFront);
    private TalonSRX leftTop = new TalonSRX(ActuatorMap.leftTop);
    private TalonSRX leftBack = new TalonSRX(ActuatorMap.leftBack);
    private TalonSRX right = new TalonSRX(ActuatorMap.rightFront);
    private TalonSRX rightTop = new TalonSRX(ActuatorMap.rightTop);
    private TalonSRX rightBack = new TalonSRX(ActuatorMap.rightBack);

    public Drive() {
        leftTop.set(ControlMode.Follower, ActuatorMap.leftFront);
        leftBack.set(ControlMode.Follower, ActuatorMap.leftFront);
        rightTop.set(ControlMode.Follower, ActuatorMap.rightFront);
        rightBack.set(ControlMode.Follower, ActuatorMap.rightFront);

        left.setInverted(false);
        leftTop.setInverted(true);
        leftBack.setInverted(true);
        right.setInverted(false);
        rightTop.setInverted(false);
        rightBack.setInverted(false);

        navX = new AHRS(SPI.Port.kMXP, (byte) 200);

        left.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 10);
        right.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 10);
    }

    public void reset() {
        left.setSelectedSensorPosition(0);
        right.setSelectedSensorPosition(0);
    }

    public void update(DriveState driveState) {
        //driveState.updateAngle(navX.getAngle());
        //driveState.updateWheelPosition(left.getSelectedSensorPosition(), right.getSelectedSensorPosition());

        if(Controls.leftTrigger()) {
            visionDrive(driveState);
        } else if(Controls.rightTrigger()) {
            left.set(ControlMode.PercentOutput, Controls.leftY());
            right.set(ControlMode.PercentOutput, Controls.rightY());
        } else {
            left.set(ControlMode.PercentOutput, 0);
            right.set(ControlMode.PercentOutput, 0);
        }
    }

    public void visionDrive(DriveState driveState) {
        visionDrive(driveState, false, true);
    }

    public void visionDrive(DriveState driveState, boolean adjustOutput, boolean adjustAngle) {
        double leftOutput = 0;
        double rightOutput = 0;
        if(adjustOutput) {
            leftOutput = driveState.getVisionDrive();
            rightOutput = driveState.getVisionDrive();
        } else {
            leftOutput = Controls.leftY();
            rightOutput = Controls.leftY();
        }
        if(adjustAngle) {
            leftOutput -= driveState.getVisionTurn();
            rightOutput += driveState.getVisionTurn();
        }
        SmartDashboard.putNumber("Left Output", leftOutput);
        SmartDashboard.putNumber("Right Output", rightOutput);
        left.set(ControlMode.PercentOutput, leftOutput);
        right.set(ControlMode.PercentOutput, rightOutput);
    }
}
