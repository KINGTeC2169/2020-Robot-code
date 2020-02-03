package frc.util.drivers;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import frc.util.Constants;

public class TalonFactory {
    public static Talon masterTalon(int id, boolean inverted) {
        Talon talon = new Talon(id);
        talon.setInverted(inverted);
        if(!Constants.usingTestBed) {
            talon.talon.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 10);
        }
        return talon;
    }

    public static Talon slaveTalon(int id, boolean inverted, Talon master) {
        Talon talon = new Talon(id);
        talon.setInverted(inverted);
        talon.makeFollower(master);
        return talon;
    }
}
