package frc.robot.commands;

import frc.util.Constants;
import frc.util.Controls;

public class TelescopeCommand {
    private static TelescopeCommand instance;
    protected static TelescopeCommand getInstance() {
        if(instance == null) {
            return instance = new TelescopeCommand();
        } else {
            return instance;
        }
    }

    private final Controls controls;

    private boolean up = false;
    private boolean extending = false;
    private boolean retracting = false;
    private boolean lb = false; // Left bumper
    private boolean pawl = true;

    private TelescopeCommand() {
        controls = Controls.getInstance();
    }

    public void teleop() {
        if(controls.xbox.getPOV() == 270) {
            up = true;
        } else if(controls.xbox.getPOV() == 90 && !extending && !retracting && pawl) {
            up = false;
        }
        if(controls.xbox.getPOV() == 0 && up && pawl) {
            extending = true;
        } else if(retracting && !lb && controls.xbox.getRawButton(5)) {
            pawl = !pawl;
        } else if(controls.xbox.getPOV() == 180 && up) {
            retracting = true;
        } else {
            extending = false;
            retracting = false;
        }
//        if(controls.xbox.getPOV() == 0) {
//            extending = true;
//        } else if(controls.xbox.getPOV() == 180) {
//            retracting = true;
//        } else {
//            extending = false;
//            retracting = false;
//        }

        lb = controls.xbox.getRawButton(5);
    }

    /* Getters */

    public boolean isTrenchMode() {
        return controls.xbox.getRawAxis(2) > Constants.trenchModeThreshold;
    }

    public boolean isExtending() {
        return extending;
    }

    public boolean isRetracting() {
        return retracting;
    }

    public boolean isUp() {
        return up;
    }

    public boolean isPawl() {
        return pawl;
    }
}
