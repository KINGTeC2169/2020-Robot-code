package frc.robot.auto.modes;

import frc.robot.auto.actions.*;

public class Owen implements Mode {
    /*
    * Shoot
    * Drive past line
    * */

    private final Series series;

    public Owen() {
        series = new Series(
                new BallJail(),
                new Wait(6),
                new BallJail(),
                new ShootBalls(),
                new Wait(.5),
                new TimedDrive(0.5)
        );
    }

    @Override
    public void start() {
        series.start();
    }

    @Override
    public void run() {
        series.run();
    }

    @Override
    public void stop() {
        series.stop();
    }

    @Override
    public boolean isRunning() {
        return series.isFinished();
    }
}
