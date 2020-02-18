package frc.robot.auto.actions;

import frc.robot.commands.DriveCommand;
import frc.robot.commands.IntakeCommand;
import frc.util.BallTracker;

public class ChaseBall implements Action {

    private final BallTracker ballTracker;
    private final DriveCommand dCommand;
    private final IntakeCommand iCommand;

    private int loopsWithoutBalls = 0;

    public ChaseBall(DriveCommand dCommand, IntakeCommand iCommand) {
        ballTracker = BallTracker.getInstance();
        this.dCommand = dCommand;
        this.iCommand = iCommand;
    }

    @Override
    public void start() {
        iCommand.setIntake();
    }

    @Override
    public void run() {
        BallTracker.Ball ball = ballTracker.getLargestBall();
        if(ball.radius == 0) {
            loopsWithoutBalls++;
            dCommand.setRotateDrive(1, 0);
        } else {
            loopsWithoutBalls = 0;
            dCommand.setRotateDrive(1, ball.position.x * 0.03);

        }
    }

    @Override
    public void stop() {
        iCommand.rest();
    }

    @Override
    public boolean isFinished() {
        return loopsWithoutBalls >= 30;
    }
}
