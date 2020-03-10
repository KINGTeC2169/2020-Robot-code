package frc.robot.auto.actions;

import edu.wpi.first.wpilibj.Timer;
import frc.robot.commands.CommandMachine;
import frc.robot.commands.IndexerCommand;
import frc.robot.commands.ShooterCommand;
import frc.robot.subsystems.Superstructure;

public class ShootBalls implements Action {
    private final Superstructure superstructure;
    private final IndexerCommand idxCommand;
    private final ShooterCommand sCommand;
    private final AimAtTarget aim;
    private final double seconds;
    private final Timer timer;

    private boolean aimed;

    public ShootBalls() {
        this(30);
    }

    public ShootBalls(double seconds) {
        superstructure = Superstructure.getInstance();
        CommandMachine commandMachine = CommandMachine.getInstance();
        idxCommand = commandMachine.getIndexerCommand();
        sCommand = commandMachine.getShooterCommand();
        aim = new AimAtTarget();

        this.seconds = seconds;
        this.timer = new Timer();
    }

    @Override
    public void start() {
        aim.start();
        aimed = false;
    }

    @Override
    public void run() {
        sCommand.aimHood(true);
        aim.run();
        if(!aimed && aim.isFinished()) {
            aimed = true;
        }
        if(superstructure.isHoodAimed() && aimed || timer.get() >= seconds) {
            idxCommand.shoot();
        } else {
            idxCommand.load();
        }
        sCommand.shoot(true);

        if(isFinished()) stop();
    }

    @Override
    public void stop() {
        sCommand.rest();
        idxCommand.load();
        aim.stop();
    }

    @Override
    public boolean isFinished() {
        return superstructure.getBallsInFeeder() == 0 && superstructure.isShotABall();
    }
}
