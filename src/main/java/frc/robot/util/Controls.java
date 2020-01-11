package frc.robot.util;

import edu.wpi.first.wpilibj.Joystick;

public class Controls {
    private static Joystick leftJoy = new Joystick(0);
    private static Joystick rightJoy = new Joystick(1);

    public static boolean leftTrigger() {
        return leftJoy.getRawButton(1);
    }

    public static boolean rightTrigger() {
        return rightJoy.getRawButton(1);
    }

    public static double leftY() {
        return leftJoy.getRawAxis(1);
    }

    public static double rightY() {
        return rightJoy.getRawAxis(1);
    }
}
