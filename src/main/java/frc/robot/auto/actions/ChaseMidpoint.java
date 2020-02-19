package frc.robot.auto.actions;

import frc.robot.commands.DriveCommand;
import frc.robot.commands.IntakeCommand;
import frc.util.BallTracker;

public class ChaseMidpoint implements Action {

    private final BallTracker ballTracker;
    private final DriveCommand dCommand;
    private final IntakeCommand iCommand;

    private int loopsWithoutBalls = 0;

    public ChaseMidpoint(DriveCommand dCommand, IntakeCommand iCommand) {
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
        BallTracker.Ball[] balls = ballTracker.getLargestTwo();
        if(balls == null) {
            loopsWithoutBalls++;
            dCommand.setRotateDrive(1, 0);
        } else {
            loopsWithoutBalls = 0;
            double averageX = balls[0].position.x/2 + balls[1].position.x/2;
            dCommand.setRotateDrive(1, averageX * 0.03);
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
