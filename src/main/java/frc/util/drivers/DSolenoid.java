package frc.util.drivers;

import edu.wpi.first.wpilibj.Solenoid;
import frc.util.ActuatorMap;
import frc.util.Constants;
import frc.util.Debug;

public class DSolenoid {
    private Solenoid solenoid;
    private String name;
    private final boolean testing;

    public DSolenoid(int id) {
        testing = Constants.usingTestBed;
        if(!testing) {
            solenoid = new Solenoid(ActuatorMap.pcm, id);
        }
    }

    public void set(boolean on) {
        if(!testing) {
            solenoid.set(on);
        } else if(name != null) {
            Debug.putNumber(name, on ? 0 : 1);
        }
    }

    public boolean get() {
        return testing ? false : solenoid.get();
    }

    public void setName(String name) {
        this.name = name;
    }
}
