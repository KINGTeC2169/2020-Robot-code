package frc.robot.subsystems;

import frc.robot.states.RobotState;

public class Superstructure {

    private static Superstructure instance;

    public static Superstructure getInstance() {
        if(instance == null) {
            return instance = new Superstructure();
        } else {
            return instance;
        }
    }

    private Drive drive = new Drive();

    public void update(RobotState state) {
        state.update();
        drive.update(state.getVisionState(), state.getDriveState());
    }

    public void reset() {
        drive.reset();
    }
}
