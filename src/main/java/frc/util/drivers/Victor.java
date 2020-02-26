package frc.util.drivers;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import frc.util.Constants;
import frc.util.Debug;

public class Victor {
    final boolean testing;

    private String name;
    private int id;
    protected VictorSPX victor;

    public Victor(int id) {
        this.id = id;
        testing = Constants.usingTestBed;
        if(!testing) {
            victor = new VictorSPX(id);
        }
    }

    protected int getId() {
        return id;
    }

    protected void setInverted(boolean invert) {
        if(!testing) {
            victor.setInverted(invert);
        }
    }

    protected void makeFollower(Talon master) {
        if(!testing) {
            victor.follow(master.talon);
        }
    }

    public void reset() {
        if(!testing) {
            victor.setSelectedSensorPosition(0);
        }
    }

    public void setOutput(double output) {
        if(!testing) {
            victor.set(ControlMode.PercentOutput, output);
        } else if(name != null) {
            Debug.putNumber(name, output);
        }
    }

    public double getSensor() {
        if(testing && name != null) {
            return Debug.getNumber(name + " encoder value");
        } else if(testing) {
            return 0;
        }
        return victor.getSelectedSensorPosition(0);
    }

    public void setName(String name) {
        this.name = name;
    }
}
