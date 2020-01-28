package frc.robot.auto.actions;

import edu.wpi.first.wpilibj.command.Command;

public class Wait implements Action {
    private int loops = 0;
    private int seconds = 0;

    public Wait(int seconds) {
        this.seconds = seconds;
    }

    @Override
    public void start() {

    }

    @Override
    public void run() {
        loops++;
    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isFinished() {
        return loops * 50 >= seconds;
    }
}
