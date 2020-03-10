package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import frc.robot.commands.TelescopeCommand;
import frc.util.ActuatorMap;
import frc.util.Constants;
import frc.util.Controls;
import frc.util.drivers.ControllerFactory;
import frc.util.drivers.DSolenoid;

public class Telescope implements Subsystem {
    private static Telescope instance;
    public static Telescope getInstance(TelescopeCommand tCommand) {
        if(!Constants.telescopeEnabled) {
            return null;
        } else if(instance == null) {
            return instance = new Telescope(tCommand);
        } else {
            return instance;
        }
    }

    private final TelescopeCommand tCommand;

    private TalonSRX master;
    private DSolenoid piston;
    private DSolenoid pawl;

    private Telescope(TelescopeCommand tCommand) {
        this.tCommand = tCommand;
        master = ControllerFactory.masterTalon(ActuatorMap.telescopingMaster, false);
        ControllerFactory.slaveVictor(ActuatorMap.telescopingSlave, false, master);
        piston = new DSolenoid(ActuatorMap.climberExtend, ActuatorMap.climberRetract);
        pawl = new DSolenoid(ActuatorMap.pawlExtend, ActuatorMap.pawlRetract);

        piston.setName("Climber Piston");
        pawl.setName("Pawl");
    }


    @Override
    public void update() {
        pawl.set(tCommand.isPawl());
        if(tCommand.isExtending()) {
            master.set(ControlMode.PercentOutput, -.5);
        } else if(tCommand.isRetracting()) {
            master.set(ControlMode.PercentOutput, 1);
        } else {
            master.set(ControlMode.PercentOutput, 0);
        }

        if(tCommand.isTrenchMode()) {
            // Ooh yeah it's trench time
            piston.set(false);
        } else {
            piston.set(tCommand.isUp());
        }
    }

    @Override
    public void reset() {
        master.set(ControlMode.PercentOutput, 0);
        piston.set(true);
    }
}
