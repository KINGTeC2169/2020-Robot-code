package frc.robot.auto.actions;

import frc.robot.commands.DriveCommand;
import frc.robot.commands.IntakeCommand;
import frc.util.BallTracker;
import frc.util.Conversion;
import frc.util.Debug;
import frc.util.drivers.NavX;

import java.text.DecimalFormat;

public class ChaseBall implements Action {

    /*
    *         beta
    * Ball---|====>-------Target
    *  \                 /
    *   \               /
    *    \             /
    *     \           /
    *      \         /
    *       \       /
    *        ^     /
    *   alpha \   /
    *          \ /
    *           Robot
    * __
    * BT = k
    * */

    private final BallTracker ballTracker;
    private final DriveCommand dCommand;
    private final IntakeCommand iCommand;
    private final NavX navX;

    private final double beta;
    private final double k;

    private int loopsWithoutBalls = 0;

    public ChaseBall(DriveCommand dCommand, IntakeCommand iCommand, double beta, double k) {
        ballTracker = BallTracker.getInstance();
        this.dCommand = dCommand;
        this.iCommand = iCommand;
        this.navX = NavX.getInstance();
        this.beta = beta;
        this.k = k;
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
            dCommand.setRotateDrive(1, 0);
        } else {
            loopsWithoutBalls = 0;

            double alpha = navX.getAngle() - ball.position.x;
            double theta = Conversion.degToRad(-beta - alpha);
            double d = 3.5 / Math.tan(Conversion.degToRad(ball.radius));
            double rt = Math.sqrt(d*d + k*k - 2*d*k*Math.cos(theta));
            double psi = Conversion.radToDeg(Math.asin(k*Math.sin(theta) / rt));
            double error = psi + ball.position.x;
            dCommand.setRotateDrive(1, error);
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
