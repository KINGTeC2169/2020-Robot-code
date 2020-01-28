package frc.robot.auto.modes;

import frc.robot.auto.actions.LinearDrive;

public class TestMode implements Mode {

    LinearDrive linearDrive = new LinearDrive(10000);

    @Override
    public void start() {
        linearDrive.start();
    }

    @Override
    public void run() {
        linearDrive.run();
        if(linearDrive.isFinished()) {
            linearDrive.stop();
        }
    }

    @Override
    public void stop() {
        linearDrive.stop();
    }
}
