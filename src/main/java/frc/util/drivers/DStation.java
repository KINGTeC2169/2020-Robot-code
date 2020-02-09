package frc.util.drivers;

import edu.wpi.first.wpilibj.DriverStation;
import frc.util.Constants;
import frc.util.Debug;

public class DStation {
    private static DStation instance;
    public static DStation getInstance() {
        if(instance == null) {
            return instance = new DStation();
        } else {
            return instance;
        }
    }

    private final boolean testing;
    private DriverStation driverStation;

    public DStation() {
        testing = Constants.usingTestBed;
        if(!testing) {
            driverStation = DriverStation.getInstance();
        }
    }

    public String getMessage() {
        if(testing) {
            return Debug.getString("Game Message");
        } else {
            return driverStation.getGameSpecificMessage();
        }
    }
}
