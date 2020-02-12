package frc.robot.auto.actions;

import frc.robot.commands.DriveCommand;
import frc.robot.commands.IndexerCommand;
import frc.robot.commands.ShooterCommand;
import frc.robot.subsystems.Superstructure;

public class ShootBalls implements Action {
    private final Superstructure superstructure;
    private final IndexerCommand idxCommand;
    private final ShooterCommand sCommand;
    private final AimAtTarget aim;

    private boolean aimed;

    public ShootBalls(Superstructure superstructure, DriveCommand dCommand, IndexerCommand idxCommand, ShooterCommand sCommand) {
        this.superstructure = superstructure;
        this.idxCommand = idxCommand;
        this.sCommand = sCommand;
        aim = new AimAtTarget(dCommand);
    }

    @Override
    public void start() {
        aim.start();
        aimed = false;
    }

    @Override
    public void run() {
        sCommand.aimHood(true);
        if(!aimed && aim.isFinished()) {
            aim.stop();
            aimed = true;
        } else if(!aimed) {
            aim.run();
        }
        if(superstructure.isHoodAimed() && aimed) {
            idxCommand.shoot();
        } else {
            idxCommand.rest();
        }
        sCommand.shoot(true);

        if(isFinished()) stop();
    }

    @Override
    public void stop() {
        sCommand.rest();
        idxCommand.rest();
        aim.stop();
    }

    @Override
    public boolean isFinished() {
        return superstructure.getBallsInFeeder() == 0;
    }
}
