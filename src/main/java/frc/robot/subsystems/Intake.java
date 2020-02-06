package frc.robot.subsystems;

public class Intake implements Subsystem {
    private Intake instance;
    public Intake getInstance() {
        if(instance == null) {
            return instance = new Intake();
        } else {
            return instance;
        }
    }

    @Override
    public void update() {

    }

    @Override
    public void reset() {

    }
}
