package frc.robot.auto.actions;

import frc.robot.commands.CommandMachine;
import frc.robot.commands.DriveCommand;
import frc.robot.subsystems.Superstructure;
import frc.util.drivers.NavX;

public class LinearDrive implements Action {
    private final Superstructure superstructure;
    private final DriveCommand dCommand;
    private final double targetAngle;
    private final double targetDistance;
    private final double turnMultiplier;

    public LinearDrive(double distance) {
        this(distance, NavX.getInstance().getAngle(), 1);
    }

    public LinearDrive(double distance, double direction) {
        this(distance, direction, 1);
    }

    public LinearDrive(double distance, double direction, double turnMultiplier) {
        superstructure = Superstructure.getInstance();
        dCommand = CommandMachine.getInstance().getDriveCommand();
        targetAngle = direction;
        targetDistance = distance;
        this.turnMultiplier = turnMultiplier;
    }

    @Override
    public void start() {
        dCommand.setLinearDrive(targetDistance, targetAngle, turnMultiplier);
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
