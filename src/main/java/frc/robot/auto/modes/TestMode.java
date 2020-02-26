package frc.robot.auto.modes;

import frc.robot.auto.actions.*;

public class TestMode implements Mode {

    private final Action action;

    private boolean running = false;

    public TestMode(int type) {

        Action[] actions = {
                // 0
                new AimAtTarget(),
                new ChaseBall(),
                new ChaseBall(60, 45),
                new ChaseBall(60, 45, 90, 7),
                new ChaseMidpoint(),
                // 5
                new ChaseMidpoint(60, 30, .2),
                new CurveToAngle(45, 5, .5, -.3),
                new GetInRange2(),
                new GetInRange3(),
                new LinearDrive(96),
                // 10
                new LinearDrive(-96, 45),
                new RunFlywheel(),
                new RunIntake(),
                new SearchTarget(),
                new ShootBalls(),
                // 15
                new TurnInPlace(0),
                new Parallel(
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
                // 20
        };
        action = new Series(actions[type]);
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
