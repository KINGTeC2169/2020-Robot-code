package frc.util.drivers;

import edu.wpi.first.wpilibj.Solenoid;
import frc.util.ActuatorMap;
import frc.util.Constants;
import frc.util.Debug;

public class Sol {
    private Solenoid solenoid;
    private String name;
    private final boolean testing;

    public Sol(int port) {
        testing = Constants.usingTestBed;
        if(!testing) {
            solenoid = new Solenoid(ActuatorMap.pcm, port);
        }
    }

    public void set(boolean on) {
        if(!testing) {
            solenoid.set(on);
        }
        if(name != null) {
            Debug.putBoolean(name, on);
        }
    }

    public void setName(String name) {
        this.name = name;
    }
}
