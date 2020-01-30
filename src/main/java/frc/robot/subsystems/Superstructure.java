package frc.robot.subsystems;

import frc.robot.states.RobotState;
import frc.robot.util.Debug;
import frc.robot.util.Limelight;

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

    private Limelight limelight = new Limelight();

    public void update(RobotState state) {
        Debug.vision(limelight);

        state.update();
        drive.update(limelight, state.getDriveState());
    }

    public void reset() {
        drive.reset();
    }
}
