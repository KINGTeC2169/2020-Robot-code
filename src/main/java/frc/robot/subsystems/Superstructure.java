package frc.robot.subsystems;

public class Superstructure {
    private final int SUBSYSTEMS = 1;

    private Subsystem[] subsystems = new Subsystem[SUBSYSTEMS];

    public Superstructure() {
        subsystems[0] = new Drive();
    }

    public void handle() {
        for(int i = 0; i < SUBSYSTEMS; i++) {
            subsystems[i].handle();
        }
    }
}
