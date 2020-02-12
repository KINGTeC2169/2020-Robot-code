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

    public void reset() {
        driveState.reset();
        intakeState.reset();
    }

    public void update() {
        driveState.update();
        intakeState.update();
    }

    public DriveState getDriveState() {
        return driveState;
    }

    public IntakeState getIntakeState() {
        return intakeState;
    }
}
