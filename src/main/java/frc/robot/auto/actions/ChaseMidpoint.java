package frc.robot.auto.actions;

import frc.robot.commands.DriveCommand;
import frc.robot.commands.IntakeCommand;
import frc.util.BallTracker;
import frc.util.Conversion;
import frc.util.drivers.NavX;

public class ChaseMidpoint implements Action {

    private final BallTracker ballTracker;
    private final DriveCommand dCommand;
    private final IntakeCommand iCommand;
    private final NavX navX;
    private final double maxD;
    private final double gamma;

    private int loopsWithoutBalls = 0;

    public ChaseMidpoint(DriveCommand dCommand, IntakeCommand iCommand) {
        this(dCommand, iCommand, 0, Double.MAX_VALUE);
    }

    public ChaseMidpoint(DriveCommand dCommand, IntakeCommand iCommand, double maxD, double gamma) {
        ballTracker = BallTracker.getInstance();
        this.dCommand = dCommand;
        this.iCommand = iCommand;
        this.navX = NavX.getInstance();
        this.maxD = maxD;
        this.gamma = gamma;
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
            linearDrive();
        } else {
            double d1 = 3.5 / Math.tan(Conversion.degToRad(balls[0].radius));
            double d2 = 3.5 / Math.tan(Conversion.degToRad(balls[1].radius));

            if(d1 < maxD && d2 < maxD) {
                loopsWithoutBalls = 0;
                double averageX = balls[0].position.x/2 + balls[1].position.x/2;
                dCommand.setRotateDrive(1, averageX * 0.03);
            } else {
                linearDrive();
            }
        }
    }

    private void linearDrive() {
        if(gamma < 180) {
            dCommand.setRotateDrive(1, navX.getAngle() - gamma);
        } else {
            dCommand.setRotateDrive(1, 0);
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
