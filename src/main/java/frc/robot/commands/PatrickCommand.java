package frc.robot.commands;

import frc.util.Controls;

public class PatrickCommand {
    private static PatrickCommand instance;
    protected static PatrickCommand getInstance() {
        if(instance == null) {
            return instance = new PatrickCommand();
        } else {
            return instance;
        }
    }

    private final Controls controls;

    private PatrickCommand() {
        controls = Controls.getInstance();
    }

    protected void teleop() {

    }

    /* Getters */

    public boolean isPositionalControl() {
        return controls.xbox.getBButton();
    }

    public boolean isRotationalControl() {
        return controls.xbox.getXButton();
    }
}
