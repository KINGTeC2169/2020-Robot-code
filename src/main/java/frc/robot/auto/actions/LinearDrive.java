package frc.robot.auto.actions;

import frc.robot.commands.DriveCommand;
import frc.robot.subsystems.Superstructure;
import frc.util.drivers.NavX;

public class LinearDrive implements Action {
    private final Superstructure superstructure;
    private final DriveCommand dCommand;
    private final double targetAngle;
    private final double targetDistance;

    public LinearDrive(Superstructure superstructure, DriveCommand dCommand, double distance) {
        this(superstructure, dCommand, NavX.getInstance().getAngle(), distance);
    }

    public LinearDrive(Superstructure superstructure, DriveCommand dCommand, double direction, double distance) {
        this.superstructure = superstructure;
        this.dCommand = dCommand;
        targetAngle = direction;
        targetDistance = distance;
    }

    @Override
    public void start() {
        dCommand.setLinearDrive(targetDistance, targetAngle);
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
        return targetDistance > 0 == (superstructure.getLinearDriveDistance() >= targetDistance); // Ah, comparing booleans
    }
}
