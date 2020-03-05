package frc.robot.auto.actions;

import frc.robot.subsystems.Superstructure;

public class GhostDrive extends Ghost {
    private final Superstructure superstructure;
    private final double distance;
    private double startingDistance;
    private boolean backwards;

    public GhostDrive(double distance) {
        superstructure = Superstructure.getInstance();
        this.distance = distance;
    }

    @Override
    public void start() {
        startingDistance = superstructure.getDriveDistance();
        backwards = distance < 0;
    }

    @Override
    public boolean isFinished() {
//        System.out.println(backwards + " " + superstructure.getDriveDistance() + " " + startingDistance + " " + distance);
        return backwards != superstructure.getDriveDistance() - startingDistance > distance;
    }
}
