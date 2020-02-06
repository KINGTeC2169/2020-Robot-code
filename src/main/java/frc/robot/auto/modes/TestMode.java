package frc.robot.auto.modes;

import frc.robot.auto.actions.AimAtTarget;
import frc.robot.auto.actions.GetInRange2;
import frc.robot.auto.actions.LookAtTarget;

public class TestMode implements Mode {

    private boolean running = false;

    AimAtTarget aimAtTarget = new AimAtTarget();
    LookAtTarget lookAtTarget = new LookAtTarget();
    GetInRange2 getInRange = new GetInRange2();

    @Override
    public void start() {
        getInRange.start();
        running = true;
    }

    private boolean sawTarget = false;

    @Override
    public void run() {
        if(getInRange.isFinished()) {
            getInRange.stop();
        } else {
            getInRange.run();
        }
    }

    @Override
    public void stop() {
        getInRange.stop();
        running = true;
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}
