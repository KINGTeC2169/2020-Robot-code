package frc.robot.auto.actions;

import frc.robot.commands.DriveCommand;
import frc.robot.commands.IndexerCommand;
import frc.robot.subsystems.Indexer;
import frc.robot.subsystems.Shooter;
import frc.robot.subsystems.Superstructure;

public class ShootBalls implements Action {
    private final Superstructure superstructure;
    private final IndexerCommand idxCommand;
    private final AimAtTarget aim;
    private final Shooter shooter;

    public ShootBalls(Superstructure superstructure, DriveCommand dCommand, IndexerCommand idxCommand) {
        this.superstructure = superstructure;
        this.idxCommand = idxCommand;
        aim = new AimAtTarget(dCommand);
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
        if(shooter.isHoodAimed()) {
            idxCommand.shoot();
        }
        shooter.shoot();

        if(isFinished()) stop();
    }

    @Override
    public void stop() {
        idxCommand.rest();
        aim.stop();
    }

    @Override
    public boolean isFinished() {
        return superstructure.getBallsInFeeder() == 0;
    }
}
