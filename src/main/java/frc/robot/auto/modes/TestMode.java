package frc.robot.auto.modes;

import frc.robot.auto.actions.GetInRange2;

public class TestMode implements Mode {

    GetInRange2 getInRange = new GetInRange2();

    @Override
    public void start() {
        getInRange.start();
    }

    @Override
    public void run() {
        getInRange.run();
        if(getInRange.isFinished()) {
            getInRange.stop();
        }
    }

    @Override
    public void stop() {
        getInRange.stop();
    }
}
