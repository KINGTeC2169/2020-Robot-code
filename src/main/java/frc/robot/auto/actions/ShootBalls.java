package frc.robot.auto.actions;

import frc.robot.subsystems.Indexer;
import frc.robot.subsystems.Shooter;

public class ShootBalls implements Action {
    private AimAtTarget aim;
    private Indexer indexer;
    private Shooter shooter;

    public ShootBalls() {
        aim = new AimAtTarget();
        indexer = Indexer.getInstance();
        shooter = Shooter.getInstance();
    }

    @Override
    public void start() {
        aim.start();
    }

    @Override
    public void run() {
        aim.run();
        shooter.aimHood(true, false);
        indexer.shoot(shooter.isHoodAimed());
        shooter.shoot();
    }

    @Override
    public void stop() {
        aim.stop();
    }

    @Override
    public boolean isFinished() {
        return indexer.getBallsInFeeder() == 0;
    }
}
