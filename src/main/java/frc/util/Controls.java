package frc.util;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;

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
    private XboxController xbox = new XboxController(2);

    public Controls() {
        testing = !Constants.usingControls;
    }

    public boolean leftButton(int button) {
        if(testing) {
            return false;
        }
        return leftJoy.getRawButton(button);
    }

    public boolean rightButton(int button) {
        if(testing) {
            return false;
        }
        return rightJoy.getRawButton(button);
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

    public double rightZ() {
        if(testing) {
            return 0;
        }
        return leftJoy.getRawAxis(2);
    }

    public double xboxLX() {
        if(testing) {
            return 0;
        }
        return xbox.getRawAxis(0);
    }

    public double xboxLY() {
        if(testing) {
            return 0;
        }
        return xbox.getRawAxis(1);
    }

    public double xboxRX() {
        if(testing) {
            return 0;
        }
        return xbox.getRawAxis(4);
    }

    public double xboxRY() {
        if(testing) {
            return 0;
        }
        return xbox.getRawAxis(5);
    }

    public double xboxLT() {
        if(testing) {
            return 0;
        }
        return xbox.getRawAxis(2);
    }

    public double xboxRT() {
        if(testing) {
            return 0;
        }
        return xbox.getRawAxis(3);
    }

    public boolean xboxA() {
        if(testing) {
            return false;
        }
        return xbox.getAButton();
    }

    public boolean xboxB() {
        if(testing) {
            return false;
        }
        return xbox.getAButton();
    }

    public boolean xboxX() {
        if(testing) {
            return false;
        }
        return xbox.getXButton();
    }

    public boolean xboxY() {
        if(testing) {
            return false;
        }
        return xbox.getYButton();
    }

    public boolean xboxYPressed() {
        if(testing) {
            return false;
        }
        return xbox.getYButtonPressed();
    }

    public boolean xboxYReleased() {
        if(testing) {
            return false;
        }
        return xbox.getYButtonPressed();
    }

    public boolean xboxLB() {
        if(testing) {
            return false;
        }
        return xbox.getAButton();
    }

    public boolean xboxRB() {
        if(testing) {
            return false;
        }
        return xbox.getAButton();
    }

    public boolean xboxBack() {
        if(testing) {
            return false;
        }
        return xbox.getBackButton();
    }

    public boolean xboxStart() {
        if(testing) {
            return false;
        }
        return xbox.getStartButton();
    }

    public boolean xboxL3() {
        if(testing) {
            return false;
        }
        return xbox.getRawButton(8);
    }

    public boolean xboxR3() {
        if(testing) {
            return false;
        }
        return xbox.getRawButton(9);
    }

    public int xboxPov() {
        if(testing) {
            return 0;
        }
        return xbox.getPOV();
    }
}
