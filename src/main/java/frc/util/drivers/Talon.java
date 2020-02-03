package frc.util.drivers;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import frc.util.Constants;
import frc.util.Debug;

public class Talon {
    final boolean testing;

    private String name;
    private int id;
    protected TalonSRX talon;

    public Talon(int id) {
        this.id = id;
        testing = Constants.usingTestBed;
        if(!testing) {
            talon = new TalonSRX(id);
        }
    }

    protected int getId() {
        return id;
    }

    protected void setInverted(boolean invert) {
        if(!testing) {
            talon.setInverted(invert);
        }
    }

    protected void makeFollower(Talon master) {
        if(!testing) {
            talon.set(ControlMode.Follower, master.getId());
        }
    }

    public void reset() {
        if(!testing) {
            talon.setSelectedSensorPosition(0);
        }
    }

    public void setOutput(double output) {
        if(!testing) {
            talon.set(ControlMode.PercentOutput, output);
        } else if(name != null) {
            Debug.putNumber(name, output);
        }
    }

    public double getSensor() {
        if(testing) {
            return 0;
        }
        return talon.getSelectedSensorPosition(0);
    }

    public void setName(String name) {
        this.name = name;
    }
}
