package frc.robot.auto.actions;

import frc.robot.commands.CommandMachine;
import frc.robot.commands.DriveCommand;
import frc.robot.commands.IntakeCommand;
import frc.util.BallTracker;
import frc.util.Constants;
import frc.util.Conversion;
import frc.util.drivers.NavX;

public class ChaseMidpoint implements Action {

    private final BallTracker ballTracker;
    private final DriveCommand dCommand;
    private final IntakeCommand iCommand;
    private final NavX navX;
    private final double maxD;
    private final double gamma;
    private final double hs; // Horizontal shift

    private int loopsWithoutBalls = 0;
    private boolean foundBalls = false;

    public ChaseMidpoint() {
        this(0, Double.MAX_VALUE, .5);
    }

    public ChaseMidpoint(double maxD) {
        this(maxD, Double.MAX_VALUE, .5);
    }

    public ChaseMidpoint(double maxD, double gamma) {
        this(maxD, gamma, .5);
    }

    public ChaseMidpoint(double maxD, double gamma, double hs) {
        ballTracker = BallTracker.getInstance();
        CommandMachine commandMachine = CommandMachine.getInstance();
        dCommand = commandMachine.getDriveCommand();
        iCommand = commandMachine.getIntakeCommand();
        this.navX = NavX.getInstance();
        this.maxD = maxD;
        this.gamma = gamma;
        this.hs = hs;
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
                foundBalls = true;

                double lesserX = Math.min(balls[0].position.x,balls[1].position.x);
                double greaterX = Math.max(balls[0].position.x,balls[1].position.x);
                double averageX = lesserX + hs * (greaterX - lesserX);
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
        return foundBalls && loopsWithoutBalls >= Constants.chaseMidpointLoops;
    }
}
