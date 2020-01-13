package frc.robot.states;

public class RobotState {
    private static RobotState instance;

    public static RobotState getInstance() {
        if(instance == null) {
            return instance = new RobotState();
        } else {
            return instance;
        }
    }

    public void update() {
        driveState.update();
    }

    private DriveState driveState = new DriveState();

    public DriveState getDriveState() {
        return driveState;
    }
}
