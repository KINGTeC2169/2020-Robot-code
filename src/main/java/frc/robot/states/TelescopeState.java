package frc.robot.states;

import frc.util.Controls;

public class TelescopeState {
    private Controls controls;
    private boolean up = false;
    private boolean extending = false;
    private boolean retracting = false;

    public TelescopeState() {
        controls = Controls.getInstance();
    }

    public void update() {
        // TODO: This control scheme is totally wrong. Fix it
        if(controls.xbox.getPOV() == 1) {
            up = true;
        } else if(controls.xbox.getPOV() == 4 && !extending) {
            up = false;
        }
        if(controls.xbox.getPOV() == 2 && up) {
            extending = true;
        } else if(controls.xbox.getPOV() == 3) {
            extending = false;
            retracting = true;
        } else {
            retracting = false;
        }
    }

    public void reset() {
        extending = false;
        retracting = false;
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
}
