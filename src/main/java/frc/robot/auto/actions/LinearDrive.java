package frc.robot.auto.actions;

import frc.robot.commands.DriveCommand;
import frc.robot.subsystems.Superstructure;

public class LinearDrive implements Action {
    private final Superstructure superstructure;
    private final DriveCommand dCommand;
    private final double targetDistance;

    public LinearDrive(Superstructure superstructure, DriveCommand dCommand, double distance) {
        this.superstructure = superstructure;
        this.dCommand = dCommand;
        targetDistance = distance;
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
        return superstructure.getLinearDriveDistance() >= targetDistance;
    }
}
