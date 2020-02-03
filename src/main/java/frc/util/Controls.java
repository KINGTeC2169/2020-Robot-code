package frc.util;

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

    private boolean testing = false;
    private Joystick leftJoy = new Joystick(0);
    private Joystick rightJoy = new Joystick(1);

    public Controls() {
        testing = Constants.usingTestBed;
    }

    public boolean leftTrigger() {
        if(testing) {
            return false;
        }
        return leftJoy.getRawButton(1);
    }

    public boolean rightTrigger() {
        if(testing) {
            return false;
        }
        return rightJoy.getRawButton(1);
    }

    public double leftY() {
        if(testing) {
            return 0;
        }
        return leftJoy.getRawAxis(1);
    }

    public double rightY() {
        if(testing) {
            return 0;
        }
        return rightJoy.getRawAxis(1);
    }
}
