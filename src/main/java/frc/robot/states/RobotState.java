package frc.robot.states;

import frc.robot.util.Limelight;

public class RobotState {
    private static RobotState instance;

    public static RobotState getInstance() {
        if(instance == null) {
            return instance = new RobotState();
        } else {
            return instance;
        }
    }

    public void reset() {
        driveState.reset();
    }

    public void update(Limelight limelight) {
        driveState.update(limelight);
    }

    private DriveState driveState = new DriveState();

    public DriveState getDriveState() {
        return driveState;
    }
}
