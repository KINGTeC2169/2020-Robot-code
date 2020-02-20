package frc.robot.auto.actions;

import frc.robot.commands.DriveCommand;
import frc.robot.commands.IntakeCommand;
import frc.util.BallTracker;
import frc.util.Conversion;
import frc.util.drivers.NavX;

public class ChaseBall implements Action {

    /*
    * Variable names for aiming for a target to the side of the ball:
    *
    *         beta
    * Ball---|====>-------Target
    *  \                 /
    *   \               /
    *    \             /
    *     \           /
    *     ^^         /
    *      \\       /
    * alpha \\     /
    *        \\   /
    *          \ /
    *           Robot
    * __
    * BT = k
    * */

    private final BallTracker ballTracker;
    private final DriveCommand dCommand;
    private final IntakeCommand iCommand;
    private final NavX navX;
    private final double maxD;
    private final double gamma;

    private final double beta;
    private final double k;

    private int loopsWithoutBalls = 0;

    public ChaseBall(DriveCommand dCommand, IntakeCommand iCommand, double beta, double k) {
        this(dCommand, iCommand, beta, k, 0, Double.MAX_VALUE);
    }

    public ChaseBall(DriveCommand dCommand, IntakeCommand iCommand, double beta, double k, double maxD) {
        this(dCommand, iCommand, beta, k, maxD, Double.MAX_VALUE);
    }

    public ChaseBall(DriveCommand dCommand, IntakeCommand iCommand, double beta, double k, double maxD, double gamma) {
        ballTracker = BallTracker.getInstance();
        this.dCommand = dCommand;
        this.iCommand = iCommand;
        this.navX = NavX.getInstance();
        this.beta = beta;
        this.k = k;
        this.maxD = maxD;
        this.gamma = gamma; // The direction the robot should head when it doesn't see a ball
    }

    @Override
    public void start() {
        iCommand.setIntake();
    }

    @Override
    public void run() {
        BallTracker.Ball ball = ballTracker.getLargestBall();
        if(ball == null) {
            loopsWithoutBalls++;
            linearDrive();
        } else if(beta != 0) {
            loopsWithoutBalls = 0;

            double d = 3.5 / Math.tan(Conversion.degToRad(ball.radius));
            if(d < maxD) {
                double alpha = navX.getAngle() - ball.position.x;
                double theta = Conversion.degToRad(-beta - alpha);
                double rt = Math.sqrt(d*d + k*k - 2*d*k*Math.cos(theta));
                double psi = Conversion.radToDeg(Math.asin(k*Math.sin(theta) / rt));
                double error = psi + ball.position.x;
                dCommand.setRotateDrive(1, error);
            } else {
                linearDrive();
            }
        } else {
            dCommand.setRotateDrive(1, ball.position.x);
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
