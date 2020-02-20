package frc.robot.auto.modes;

import frc.robot.auto.actions.*;

public class Verdun implements Mode {

    private final Series main;

    public Verdun() {
        main = new Series(
                new ShootBalls(),
                new Parallel(
                        new RunIntake(),
                        new Series(
                                new ChaseBall(72, 30, -90, 7),
                                new ChaseBall(48, 10, -90, 3),
                                new ChaseBall(48, 5),
                                new ChaseMidpoint(72)
                        )
                ),
                new Parallel(
                        new RunFlywheel(),
                        new Series(
                                new Parallel(
                                        new LinearDrive(-96, 0),
                                        new FindTarget()
                                ),
                                new GetInRange3()
                        )
                ),
                new ShootBalls()
        );
    }

    @Override
    public void start() {
        main.start();
    }

    @Override
    public void run() {
        main.run();
    }

    @Override
    public void stop() {
        main.stop();
    }

    @Override
    public boolean isRunning() {
        return false;
    }
}
