package frc.util.drivers;

import edu.wpi.first.wpilibj.DigitalInput;
import frc.util.Constants;
import frc.util.Debug;

public class DInput {
    private final boolean testing;
    private final DigitalInput dinput;
    private String name;

    public DInput(int channel) {
        testing = Constants.usingTestBed;
        dinput = new DigitalInput(channel);
    }

    public boolean get() {
        if(!testing) {
            Debug.putBoolean(name, dinput.get());
            return dinput.get();
        } else if(name != null) {
            return Debug.getBoolean(name);
        }
        return false;
    }

    public void setName(String name) {
        this.name = name;
    }
}
