package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SPI;
import frc.robot.states.DriveState;
import frc.robot.states.RobotState;
import frc.robot.util.*;

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
    private AHRS navX;
    private PD visionDrive;

    // Master talons
    private TalonSRX left = new TalonSRX(ActuatorMap.leftFront);
    private TalonSRX right = new TalonSRX(ActuatorMap.rightFront);

    public Drive() {
        controls = Controls.getInstance();
        limelight = Limelight.getInstance();
        driveState = RobotState.getInstance().getDriveState();

        // Slave talons
        TalonSRX leftTop = new TalonSRX(ActuatorMap.leftTop);
        TalonSRX leftBack = new TalonSRX(ActuatorMap.leftBack);
        TalonSRX rightTop = new TalonSRX(ActuatorMap.rightTop);
        TalonSRX rightBack = new TalonSRX(ActuatorMap.rightBack);

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

        visionDrive = new PD(Constants.visionDriveP, Constants.visionDriveD);
    }

    @Override
    public void update() {
        driveState.updateAngle(navX.getAngle());
        driveState.updateWheelPosition(getLeftRotations(), getRightRotations());

        if(controls.leftTrigger()) {
            visionDrive();
        } else {
            left.set(ControlMode.PercentOutput, controls.leftY());
            right.set(ControlMode.PercentOutput, controls.rightY());
        }
    }

    @Override
    public void reset() {
        left.setSelectedSensorPosition(0);
        right.setSelectedSensorPosition(0);
    }

    // Aim at the target
    public void visionDrive() {
        if(limelight.isValidTarget()) {
            double output = visionDrive.getOutput(limelight.getCenter().x);
            left.set(ControlMode.PercentOutput,  controls.leftY() - output);
            right.set(ControlMode.PercentOutput, controls.leftY() + output);
        } else {
            setOutput(0, 0);
        }
    }

    public void setOutput(double l, double r) {
        left.set(ControlMode.PercentOutput, l);
        right.set(ControlMode.PercentOutput, r);
    }

    public double getLeftRotations() {
        return Conversion.encoderTicksToRotations(left.getSelectedSensorPosition(0));
    }

    public double getRightRotations() {
        return Conversion.encoderTicksToRotations(right.getSelectedSensorPosition(0));
    }

    public double getAngle() {
        return navX.getAngle();
    }
}
