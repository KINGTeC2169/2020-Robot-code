package frc.robot.auto.actions;

import frc.robot.commands.DriveCommand;

public class LinearDrive implements Action {
    private final DriveCommand dCommand;
    private final double targetDistance;

    private double distance;

    public LinearDrive(DriveCommand dCommand, double distance) {
        this.dCommand = dCommand;
        targetDistance = distance;
    }

    public void update(double distance) {
        this.distance = distance;
    }

    @Override
    public void start() {
        dCommand.setLinearDrive(targetDistance);
    }

    @Override
    public void run() {
        if(isFinished()) stop();
    }

    @Override
    public void stop() {
        dCommand.rest();
    }

    @Override
    public boolean isFinished() {
        return distance >= targetDistance;
    }
}
