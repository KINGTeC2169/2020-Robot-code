package frc.robot.auto.actions;

import edu.wpi.first.wpilibj.Timer;

public class Wait implements Action {
    private Timer timer;
    private double seconds;

    public Wait(double seconds) {
        timer = new Timer();
        this.seconds = seconds;
    }

    @Override
    public void start() {
        timer.start();
    }

    @Override
    public void run() {
    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isFinished() {
        return timer.get() >= seconds;
    }
}
