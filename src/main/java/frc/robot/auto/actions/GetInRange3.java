package frc.robot.auto.actions;

import frc.util.Constants;

public class GetInRange3 implements Action {
    GetInRange getInRange = new GetInRange(Constants.shootingMaxD3);

    @Override
    public void start() {
        getInRange.start();
    }

    @Override
    public void run() {
        getInRange.run();
    }

    @Override
    public void stop() {
        getInRange.stop();
    }

    @Override
    public boolean isFinished() {
        return getInRange.isFinished();
    }
}
