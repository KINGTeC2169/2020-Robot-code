package frc.util.drivers;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import frc.util.Constants;
import frc.util.Debug;

public class Talon {
    final boolean testing;

    private String name;
    private int id;
    private int sensorTicks = Integer.MIN_VALUE;
    private double output = 0;
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

    public void zeroSensor() {
        if(!testing) {
            talon.setSelectedSensorPosition(0);
        } else {
            if(sensorTicks != Integer.MIN_VALUE) {
                sensorTicks = 0;
            }
            Debug.putNumber(name + " encoder value", 0);
        }
    }

    public void setOutput(double output) {
        this.output = output;
        if(!testing) {
            talon.set(ControlMode.PercentOutput, output);
        } else if(name != null) {
            Debug.putNumber(name, output);
        }
    }

    public double getOutput() {
        return output;
    }

    public double getSensor() {
        if(testing && name != null) {
            if(sensorTicks == Integer.MIN_VALUE) {
                return Debug.getNumber(name + " encoder value");
            } else {
                Debug.putNumber(name + " encoder value", sensorTicks);
                return sensorTicks;
            }
        } else if(testing) {
            return 0;
        }
        return talon.getSelectedSensorPosition(0);
    }

    public double getVelocity() {
        if(testing && name != null) {
            return Debug.getNumber(name + " encoder vel.");
        } else if(testing) {
            return 0;
        }
        return talon.getSelectedSensorVelocity(0);
    }

    public void changeSensor(int ticks) {
        if(sensorTicks == Integer.MIN_VALUE) {
            sensorTicks = 0;
        }
        sensorTicks += ticks;
    }

    public void setName(String name) {
        this.name = name;
    }
}
