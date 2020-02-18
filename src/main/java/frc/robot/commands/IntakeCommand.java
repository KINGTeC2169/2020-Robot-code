package frc.robot.commands;

import frc.util.Controls;

public class IntakeCommand {
    private static IntakeCommand instance;
    protected static IntakeCommand getInstance() {
        if(instance == null) {
            return instance = new IntakeCommand();
        } else {
            return instance;
        }
    }

    private final Controls controls;

    private boolean piston = true;
    private boolean forceIntake = false;

    private IntakeCommand() {
        controls = Controls.getInstance();
    }

    protected void teleop() {
        if(controls.yButton()) {
            piston = !piston;
        }
    }

    /* Auto stuff */

    public void setIntake() {
        forceIntake = true;
    }

    public void rest() {
        forceIntake = false;
    }

    /* Getters */

    public boolean piston() {
        return piston;
    }

    public boolean intake() {
        return controls.right.getRawButton(1) || forceIntake;
    }

    public boolean exhaust() {
        return controls.xbox.getStartButton();
    }
}
