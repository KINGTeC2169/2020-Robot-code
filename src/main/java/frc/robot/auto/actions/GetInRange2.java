package frc.robot.auto.actions;

import frc.util.Constants;

public class GetInRange2 implements Action {
    GetInRange getInRange = new GetInRange(Constants.shootingMaxD2);

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
