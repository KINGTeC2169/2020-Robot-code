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
    public final Joystick left = new Joystick(0);
    public final Joystick right = new Joystick(1);
    public final XboxController xbox = new XboxController(2);

    private boolean holdingY = false;
    public boolean yButton() {
        if(!holdingY && xbox.getYButton()) {
            return holdingY = true;
        } else if(!xbox.getYButton()) {
            holdingY = false;
        }
        return false;
    }

    public Controls() {
        testing = !Constants.usingControls;
    }
}
