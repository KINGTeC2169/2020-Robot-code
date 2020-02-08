package frc.robot.states;

import frc.util.Constants;

public class IntakeState {
    public IntakeState() {

    }

    private int loopCount = 0;
    private int lastLoopRunning = (int) (-Constants.feederIntakeDelay * 50);
    private boolean running = false; // Currently running
    private boolean wasRunning = false;

    public void setRunning(boolean running) {
        this.running = running;
    }

    public boolean wasRunning() {
        return wasRunning;
    }

    public void update() {
        if(running) {
            lastLoopRunning = loopCount;
            wasRunning = true;
        } else {
            wasRunning = .02 * (loopCount - lastLoopRunning) < Constants.feederIntakeDelay;
        }
        loopCount++;
    }

    public void reset() {
        loopCount = 0;
        lastLoopRunning = (int) (-Constants.feederIntakeDelay * 50);
        wasRunning = false;
    }
}
