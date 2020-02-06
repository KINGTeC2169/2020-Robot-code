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

    public RobotState() {
    }

    private DriveState driveState = new DriveState();
    private TelescopeState telescopeState = new TelescopeState();

    public void reset() {
        driveState.reset();
        telescopeState.reset();
    }

    public void update() {
        driveState.update();
        telescopeState.update();
    }

    public DriveState getDriveState() {
        return driveState;
    }

    public TelescopeState getTelescopeState() {
        return telescopeState;
    }
}
