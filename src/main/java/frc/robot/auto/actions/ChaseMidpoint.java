package frc.robot.auto.actions;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SPI;
import frc.robot.commands.CommandMachine;
import frc.robot.commands.DriveCommand;
import frc.util.BallTracker;
import frc.util.Constants;
import frc.util.Conversion;

public class ChaseMidpoint implements Action {

    private final GhostDrive ghostDrive;
    private final BallTracker ballTracker;
    private final DriveCommand dCommand;
    private final AHRS navX;
    private final double maxD;
    private final double gamma;
    private final double hs; // Horizontal shift

    private BallTracker.Ball[] balls = null;
    private int loopsWithoutBalls = 0;

    public ChaseMidpoint() {
        this(0, Double.MAX_VALUE, .5, 96);
    }

    public ChaseMidpoint(double maxD) {
        this(maxD, Double.MAX_VALUE, .5, 96);
    }

    public ChaseMidpoint(double maxD, double gamma) {
        this(maxD, gamma, .5, 96);
    }

    public ChaseMidpoint(double maxD, double gamma, double hs) {
        this(maxD, gamma, hs, 96);
    }

    public ChaseMidpoint(double maxD, double gamma, double hs, double linearDriveDistance) {
        ghostDrive = new GhostDrive(linearDriveDistance);
        ballTracker = BallTracker.getInstance();
        CommandMachine commandMachine = CommandMachine.getInstance();
        dCommand = commandMachine.getDriveCommand();
        this.navX = new AHRS(SPI.Port.kMXP, (byte) 200);
        this.maxD = maxD;
        this.gamma = gamma;
        this.hs = hs;
    }

    @Override
    public void start() {
        ghostDrive.start();
    }

    @Override
    public void run() {
        BallTracker.Ball[] balls = ballTracker.getLargestTwo();
        if(balls == null) {
            if(this.balls != null) loopsWithoutBalls++;
            linearDrive();
        } else {
            double d1 = 3.5 / Math.tan(Conversion.degToRad(balls[0].radius));
            double d2 = 3.5 / Math.tan(Conversion.degToRad(balls[1].radius));

            if(d1 < maxD && d2 < maxD && (this.balls == null || this.balls[0].idx.equals(balls[0].idx) && this.balls[1].idx.equals(balls[1].idx))) {
                loopsWithoutBalls = 0;
                if(this.balls == null) {
                    this.balls = balls;
                }

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
        dCommand.rest();
    }

    @Override
    public boolean isFinished() {
        return loopsWithoutBalls >= Constants.chaseMidpointLoops || ghostDrive.isFinished();
    }
}
