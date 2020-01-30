package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SPI;
import frc.robot.states.DriveState;
import frc.robot.util.ActuatorMap;
import frc.robot.util.Controls;
import frc.robot.util.Limelight;
import frc.robot.util.geometry.Vector2;

public class Drive{
    private static Drive instance;
    private AHRS navX;

    private Double lastTx;

    public static Drive getInstance() {
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

    public void update(Limelight limelight, DriveState driveState) {
        driveState.updateAngle(navX.getAngle());
        driveState.updateWheelPosition(left.getSelectedSensorPosition(), right.getSelectedSensorPosition());

        if(Controls.leftTrigger()) {
            visionDrive(limelight);
        } else {
            left.set(ControlMode.PercentOutput, Controls.leftY());
            right.set(ControlMode.PercentOutput, Controls.rightY());
        }
    }

    public void visionDrive(Limelight limelight) {
        double tx = limelight.getCenter().x;
        if (lastTx == null) {
            lastTx = tx;
        }
        final double PROPORTIONAL = .01;
        final double DERIVATIVE = .0;

        if(limelight.isValidTarget()) {

            left.set(ControlMode.PercentOutput,  Controls.leftY() - PROPORTIONAL * tx - (lastTx - tx) * DERIVATIVE);
            right.set(ControlMode.PercentOutput, Controls.leftY() + PROPORTIONAL * tx + (lastTx - tx) * DERIVATIVE);
        } else {
            left.set(ControlMode.PercentOutput, 0);
            right.set(ControlMode.PercentOutput, 0);
        }

        lastTx = tx;
    }

    public void setOutput(double l, double r) {
        left.set(ControlMode.PercentOutput, l);
        right.set(ControlMode.PercentOutput, r);
    }

    public double getLeftSensor() {
        return left.getSelectedSensorPosition(0);
    }

    public double getRightSensor() {
        return right.getSelectedSensorPosition(0);
    }

    public double getAngle() {
        return navX.getAngle();
    }
}
