package frc.util.drivers;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SPI;
import frc.util.Constants;
import frc.util.Debug;

public class NavX {
    private static NavX instance;
    public static NavX getInstance() {
        if(instance == null) {
            return instance = new NavX();
        } else {
            return instance;
        }
    }

    private AHRS navX;
    private final boolean testing;

    private NavX() {
        testing = Constants.usingTestBed;
        if(!testing) {
            navX = new AHRS(SPI.Port.kMXP, (byte) 200);
        }
    }

    public double getAngle() {
        if(testing) {
            return Debug.getNumber("navX angle");
        } else {
            double angle = navX.getAngle() % 360;
            if(angle > 180) angle -= 360;
            return angle;
        }
    }
}
