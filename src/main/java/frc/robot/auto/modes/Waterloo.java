package frc.robot.auto.modes;

import frc.robot.auto.actions.*;

public class Waterloo implements Mode{

    private final Series main;

    public Waterloo(){
        main = new Series(
                new ShootBalls(),
                new LinearDrive(66, -45),
                new Parallel(
                    new RunIntake(),
                    new Series(
                            new ChaseMidpoint(45, 48, .2),
                            new ChaseBall(9, -30, 36, 60),
                            new ChaseMidpoint(60, 72 )
                    )
                ),
                new Parallel(
                    new RunFlywheel(),
                    new Series(
                        new Series(
                            new Parallel(
                                   new LinearDrive(54, 90),
                                    new FindTarget()
                            ),
                            new GetInRange3()
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
