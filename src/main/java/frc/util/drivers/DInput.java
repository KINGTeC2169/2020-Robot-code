package frc.util.drivers;

import edu.wpi.first.wpilibj.DigitalInput;
import frc.util.Constants;
import frc.util.Debug;

public class DInput {
    private final boolean testing;
    private final DigitalInput dinput;

    private boolean realState = false;
    private double loopsWithNewState = 0;
    private String name;

    public DInput(int channel) {
        testing = Constants.usingTestBed;
        dinput = new DigitalInput(channel);
    }

    public boolean get() {
        if(!testing) {
            Debug.putBoolean(name, dinput.get());
            if(dinput.get() != realState && ++loopsWithNewState >= Constants.antiBounce) {
                realState = dinput.get();
            } else {
                loopsWithNewState = 0;
            }
            return realState;
        } else if(name != null) {
            return Debug.getBoolean(name);
        }
        return false;
    }

    public void setName(String name) {
        this.name = name;
    }
}
