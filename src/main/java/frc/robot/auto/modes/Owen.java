package frc.robot.auto.modes;

import frc.robot.auto.actions.LinearDrive;
import frc.robot.auto.actions.Series;
import frc.robot.auto.actions.ShootBalls;
import frc.robot.commands.CommandMachine;
import frc.robot.subsystems.Superstructure;

public class Owen implements Mode {
    /*
    * Shoot
    * Drive past line
    * */

    private final Series series;

    public Owen() {
        series = new Series(
                new ShootBalls(),
                new LinearDrive(48, 0)
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
