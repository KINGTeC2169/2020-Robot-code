package frc.robot.util;

import edu.wpi.first.wpilibj.Joystick;

public class Controls {
    private static Controls instance;
    public static Controls getInstance() {
        if(instance == null) {
            return instance = new Controls();
        } else {
            return instance;
        }
    }

    private Joystick leftJoy = new Joystick(0);
    private Joystick rightJoy = new Joystick(1);

    public boolean leftTrigger() {
        return leftJoy.getRawButton(1);
    }

    public boolean rightTrigger() {
        return rightJoy.getRawButton(1);
    }

    public double leftY() {
        return leftJoy.getRawAxis(1);
    }

    public double rightY() {
        return rightJoy.getRawAxis(1);
    }
}
