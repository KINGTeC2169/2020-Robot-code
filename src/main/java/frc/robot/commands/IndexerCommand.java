package frc.robot.commands;

import frc.util.Constants;
import frc.util.Controls;

public class IndexerCommand {
    private static IndexerCommand instance;
    protected static IndexerCommand getInstance() {
        if(instance == null) {
            return instance = new IndexerCommand();
        } else {
            return instance;
        }
    }

    private enum LoadMode {REST, LOAD, SHOOT}

    private final Controls controls;

    private int cyclesSinceIntake = Integer.MAX_VALUE/2; // We have to do the /2 to avoid overflow
    private LoadMode loadMode = LoadMode.REST;

    private IndexerCommand() {
        controls = Controls.getInstance();
    }

    protected void teleop() {
        if(controls.xbox.getRawButton(6)) {
            loadMode = LoadMode.SHOOT;
        } else {
            loadMode = LoadMode.LOAD;
        }

        if(controls.right.getRawButton(1) && !controls.xbox.getStartButton()) {
            cyclesSinceIntake = 0;
        } else {
            cyclesSinceIntake++;
        }
    }

    protected void reset() {
        loadMode = LoadMode.LOAD;
    }

    public void load() {
        loadMode = LoadMode.LOAD;
    }

    public void shoot() {
        loadMode = LoadMode.SHOOT;
    }

    /* Getters */

    public boolean isLoad() {
        return loadMode == LoadMode.LOAD;
    }

    public boolean isShoot() {
        return loadMode == LoadMode.SHOOT;
    }

    public boolean isRunFunnel() {
        return cyclesSinceIntake < 50 * Constants.feederIntakeDelay;
    }
}
