package frc.util.drivers;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Solenoid;
import frc.util.ActuatorMap;
import frc.util.Constants;
import frc.util.Debug;

public class DSolenoid {
    private DoubleSolenoid solenoid;
    private String name;
    private final boolean testing;

    public DSolenoid(int extend, int retract) {
        testing = Constants.usingTestBed;
        if(!testing) {
            solenoid = new DoubleSolenoid(ActuatorMap.pcm, extend, retract);
        }
    }

    public void set(boolean extend) {
        DoubleSolenoid.Value value = extend ? DoubleSolenoid.Value.kForward : DoubleSolenoid.Value.kReverse;
        if(!testing) {
            solenoid.set(value);
        }
        if(name != null) {
            Debug.putBoolean(name, extend);
        }
    }

    public void setName(String name) {
        this.name = name;
    }
}
