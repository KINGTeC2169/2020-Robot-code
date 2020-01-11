package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import frc.robot.util.ActuatorMap;
import frc.robot.util.Controls;

public class Drive implements Subsystem {
    private Drive instance;

    private TalonSRX leftFront = new TalonSRX(ActuatorMap.leftFront);
    private TalonSRX leftTop = new TalonSRX(ActuatorMap.leftTop);
    private TalonSRX leftBack = new TalonSRX(ActuatorMap.leftBack);
    private TalonSRX rightFront = new TalonSRX(ActuatorMap.rightFront);
    private TalonSRX rightTop = new TalonSRX(ActuatorMap.rightTop);
    private TalonSRX rightBack = new TalonSRX(ActuatorMap.rightBack);

    public Drive() {
        leftTop.set(ControlMode.Follower, ActuatorMap.leftFront);
        leftBack.set(ControlMode.Follower, ActuatorMap.leftFront);
        rightTop.set(ControlMode.Follower, ActuatorMap.rightFront);
        rightBack.set(ControlMode.Follower, ActuatorMap.rightFront);

        leftFront.setInverted(false);
        leftTop.setInverted(true);
        leftBack.setInverted(true);
        rightFront.setInverted(false);
        rightTop.setInverted(false);
        rightBack.setInverted(false);
    }

    @Override
    public Subsystem getInstance() {
        if(instance == null) {
            return instance = new Drive();
        } else {
            return instance;
        }
    }

    @Override
    public void handle() {
        leftFront.set(ControlMode.PercentOutput, Controls.leftY());
        rightFront.set(ControlMode.PercentOutput, Controls.rightY());
    }
}
