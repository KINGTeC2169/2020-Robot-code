package frc.util.drivers;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import frc.util.ActuatorMap;
import frc.util.Constants;
import frc.util.Debug;

public class DSolenoid {
    private DoubleSolenoid solenoid;
    private String name;
    private final boolean testing;

    public DSolenoid(int retract, int extend) {
        testing = Constants.usingTestBed;
        if(!testing) {
            solenoid = new DoubleSolenoid(ActuatorMap.pcm, extend, retract);
        }
    }

    public void set(boolean on) {
        DoubleSolenoid.Value value = on ? DoubleSolenoid.Value.kForward : DoubleSolenoid.Value.kReverse;
        if(!testing) {
            solenoid.set(value);
        } else if(name != null) {
            Debug.putBoolean(name, on);
        }
    }

    public void setName(String name) {
        this.name = name;
    }
}
