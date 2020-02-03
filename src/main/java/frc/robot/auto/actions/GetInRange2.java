package frc.robot.auto.actions;

import frc.util.Constants;

public class GetInRange2 implements Action {
    GetInRange getInRange = new GetInRange(Constants.shootingMinY2, Constants.shootingMaxY2, Constants.shootingMaxSlope2);

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
