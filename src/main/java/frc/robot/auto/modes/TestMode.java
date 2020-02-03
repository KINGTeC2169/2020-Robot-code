package frc.robot.auto.modes;

import frc.robot.auto.actions.AimAtTarget;
import frc.robot.auto.actions.GetInRange2;
import frc.robot.auto.actions.LookAtTarget;

public class TestMode implements Mode {

    AimAtTarget aimAtTarget = new AimAtTarget();
    LookAtTarget lookAtTarget = new LookAtTarget();
    GetInRange2 getInRange = new GetInRange2();

    @Override
    public void start() {
        lookAtTarget.start();
    }

    private boolean sawTarget = false;

    @Override
    public void run() {
        if(lookAtTarget.isFinished()) {
            lookAtTarget.stop();
        } else {
            lookAtTarget.run();
        }
    }

    @Override
    public void stop() {
        lookAtTarget.stop();
    }
}
