package frc.util.drivers;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import frc.util.Constants;

public class ControllerFactory {
    public static TalonSRX masterTalon(int id, boolean inverted) {
        TalonSRX talon = new TalonSRX(id);
        talon.setInverted(inverted);
        talon.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 10);
        return talon;
    }

    public static TalonSRX slaveTalon(int id, boolean inverted, TalonSRX master) {
        TalonSRX talon = new TalonSRX(id);
        talon.setInverted(inverted);
        talon.set(ControlMode.Follower, master.getDeviceID());
        return talon;
    }

    public static VictorSPX victor(int id, boolean inverted) {
        VictorSPX victor = new VictorSPX(id);
        victor.setInverted(inverted);
        return victor;
    }

    public static VictorSPX slaveVictor(int id, boolean inverted, TalonSRX master) {
        VictorSPX victor = new VictorSPX(id);
        victor.setInverted(inverted);
        victor.follow(master);
        return victor;
    }
}
