package frc.robot.auto.actions;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SPI;
import frc.robot.commands.CommandMachine;
import frc.robot.commands.DriveCommand;
import frc.robot.subsystems.Superstructure;
import frc.util.BallTracker;
import frc.util.Constants;
import frc.util.Conversion;
import frc.util.Debug;

import java.text.DecimalFormat;

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

    private final GhostDrive ghostDrive;
    private final Superstructure superstructure;
    private final BallTracker ballTracker;
    private final DriveCommand dCommand;
    private final AHRS navX;
    private final double maxD;
    private final double gamma;
    private final double beta;
    private final double k;

    private BallTracker.Ball ball = null;
    private int loopsWithoutBalls = 0;

    public ChaseBall() {
        this(0, Double.MAX_VALUE, 0, 0, 96);
    }

    public ChaseBall(double maxD) {
        this(maxD, Double.MAX_VALUE, 0, 0, 96);
    }

    public ChaseBall(double maxD, double gamma) {
        this(maxD, gamma, 0, 0, 96);
    }

    public ChaseBall(double maxD, double gamma, double beta, double k) {
        this(maxD, gamma, beta, k, 96);
    }

    public ChaseBall(double maxD, double gamma, double beta, double k, double linearDriveDistance) {
        ghostDrive = new GhostDrive(linearDriveDistance);
        superstructure = Superstructure.getInstance();
        ballTracker = BallTracker.getInstance();
        CommandMachine commandMachine = CommandMachine.getInstance();
        dCommand = commandMachine.getDriveCommand();
        navX = new AHRS(SPI.Port.kMXP, (byte) 200);
        this.beta = beta;
        this.k = k;
        this.maxD = maxD;
        this.gamma = gamma; // The direction the robot should head when it doesn't see a ball
    }

    @Override
    public void start() {
        ghostDrive.start();
    }

    @Override
    public void run() {
        BallTracker.Ball ball = ballTracker.getLargestBall();
        if(ball == null) {
            // If we can't see a ball, drive straight
            if(this.ball != null) loopsWithoutBalls++;
            linearDrive();
        } else if(k != 0) {
            double d = 3.5 / Math.tan(Conversion.degToRad(ball.radius)); // Distance to ball
            DecimalFormat f = new DecimalFormat("#00.0");
            Debug.putString("dist", f.format(d));

            // If we want to track this ball
            if(d < maxD && (this.ball == null || this.ball.idx.equals(ball.idx))) {
                loopsWithoutBalls = 0;
                if(this.ball == null) {
                    this.ball = ball; // Lock onto that boi
                }

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
            loopsWithoutBalls = 0;
            this.ball = ball;

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
        dCommand.rest();
    }

    @Override
    public boolean isFinished() {
        return loopsWithoutBalls >= Constants.chaseBallLoops || ghostDrive.isFinished();
    }
}
