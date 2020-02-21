package frc.robot.auto.modes;

import frc.robot.auto.actions.*;

public class TestMode implements Mode {

    private final Action action;

    private boolean running = false;

    public TestMode(int type) {

        Action[] actions = {
                new AimAtTarget(),                                          // 0
                new ChaseBall(),
                new Parallel(new ChaseBall(60, 45)),
                new ChaseBall(60, 45, 90, 7),
                new ChaseMidpoint(),
                new ChaseMidpoint(60, 30, .2),           // 5
                new GetInRange2(),
                new GetInRange3(),
                new LinearDrive(96),
                new LinearDrive(-96, 45),
                new RunFlywheel(),                                          // 10
                new RunIntake(),
                new SearchTarget(),
                new ShootBalls(),
                new TurnInPlace(0),
                new Parallel(                                               // 15
                        new Wait(3),
                        new RunIntake()
                ),
                new Series(
                        new Wait(3),
                        new RunIntake()
                ),
                new Series(
                        new FindBall(),
                        new Parallel(
                                new Wait(3),
                                new RunIntake()
                        )
                ),
                new Series(
                        new FindTarget(),
                        new Parallel(
                                new Wait(3),
                                new RunIntake()
                        )
                )
        };
        action = actions[type];
    }

    @Override
    public void start() {
        action.start();
        running = true;
    }

    @Override
    public void run() {
        action.run();
    }

    @Override
    public void stop() {
        action.stop();
        running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}
