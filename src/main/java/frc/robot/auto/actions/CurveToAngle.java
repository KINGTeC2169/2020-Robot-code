package frc.robot.auto.actions;

import frc.robot.commands.CommandMachine;
import frc.robot.commands.DriveCommand;
import frc.util.drivers.NavX;

public class CurveToAngle implements Action {
    private final DriveCommand dCommand;
    private final NavX navX;
    private final double angle;
    private final double error;
    private final double throttle;
    private final double wheel;

    private boolean finished = false;

    public CurveToAngle(double angle, double error, double throttle, double wheel) {
        dCommand = CommandMachine.getInstance().getDriveCommand();
        navX = NavX.getInstance();
        this.angle = angle;
        this.error = error;
        this.throttle = throttle;
        this.wheel = wheel;
    }

    @Override
    public void start() {

    }

    @Override
    public void run() {
        if(finished) return;
        if(Math.abs(angle - navX.getAngle()) < error) {
            finished = true;
            stop();
        } else {
            dCommand.setCheesy(throttle, wheel);
        }
    }

    @Override
    public void stop() {
        dCommand.rest();
    }

    @Override
    public boolean isFinished() {
        return finished;
    }
}
