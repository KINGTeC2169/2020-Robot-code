package frc.robot.states;

import frc.util.Controls;

public class TelescopeState {
    private Controls controls;
    private boolean up = false;
    private boolean extending = false;
    private boolean retracting = false;
    private boolean pawl = true;

    public TelescopeState() {
        controls = Controls.getInstance();
    }

    public void update() {
        if(controls.xbox.getPOV() == 270) {
            up = true;
        } else if(controls.xbox.getPOV() == 90 && !extending && !retracting && pawl) {
            up = false;
        }
        if(controls.xbox.getPOV() == 0 && up && pawl) {
            extending = true;
        } else if(retracting && controls.xbox.getRawButton(5)) {
            pawl = false;
        } else if(controls.xbox.getPOV() == 180 && up) {
            retracting = true;
        } else {
            extending = false;
            retracting = false;
        }
    }

    public void reset() {
        extending = false;
        retracting = false;
        pawl = true;
        up = false;
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
