package frc.robot.auto.modes;

import frc.robot.auto.actions.AimAtTarget;
import frc.robot.auto.actions.GetInRange2;
import frc.robot.commands.CommandMachine;

public class TestMode implements Mode {

    private final AimAtTarget aimAtTarget;
    private final GetInRange2 getInRange = new GetInRange2();

    private boolean running = false;

    public TestMode(CommandMachine commandMachine) {
        aimAtTarget = new AimAtTarget(commandMachine.getDriveCommand());
    }

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
