package frc.robot.commands;

import frc.util.Constants;
import frc.util.Controls;

public class ShooterCommand {
    private static ShooterCommand instance;
    protected static ShooterCommand getInstance() {
        if(instance == null) {
            return instance = new ShooterCommand();
        } else {
            return instance;
        }
    }

    private final Controls controls;

    private boolean aimHood = false;
    private boolean shoot = false;

    public ShooterCommand() {
        controls = new Controls();
    }

    protected void teleop() {

    }

    /* Auto */

    public void aimHood(boolean on) {
        aimHood = on;
    }

    public void shoot(boolean on) {
        shoot = on;
    }

    public void rest() {
        aimHood = false;
        shoot = false;
    }

    /* Getters */

    public double getOutput() {
        if(shoot) {
            return 1;
        } else {
            return controls.xbox.getRawAxis(3);
        }
    }

    public boolean isTrenchMode() {
        return controls.xbox.getRawAxis(2) > Constants.trenchModeThreshold;
    }

    public boolean isAimHood() {
        if(aimHood) {
            return true;
        } else {
            return controls.right.getRawButton(2);
        }
    }
}
