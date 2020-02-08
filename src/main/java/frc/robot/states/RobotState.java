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
    private IntakeState intakeState = new IntakeState();
    private TelescopeState telescopeState = new TelescopeState();

    public void reset() {
        driveState.reset();
        intakeState.reset();
        telescopeState.reset();
    }

    public void update() {
        driveState.update();
        intakeState.update();
        telescopeState.update();
    }

    public DriveState getDriveState() {
        return driveState;
    }

    public IntakeState getIntakeState() {
        return intakeState;
    }

    public TelescopeState getTelescopeState() {
        return telescopeState;
    }
}
