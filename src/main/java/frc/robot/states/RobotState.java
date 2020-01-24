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

    public void reset() {
        driveState.reset();
    }

    public void update() {
        driveState.update();
        visionState.update();
    }

    private DriveState driveState = new DriveState();
    private VisionState visionState = new VisionState();

    public DriveState getDriveState() {
        return driveState;
    }
    public VisionState getVisionState() {return visionState;}
}
