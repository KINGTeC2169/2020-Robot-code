package frc.robot.subsystems;

import frc.robot.commands.TelescopeCommand;
import frc.util.ActuatorMap;
import frc.util.Constants;
import frc.util.Controls;
import frc.util.drivers.ControllerFactory;
import frc.util.drivers.DSolenoid;
import frc.util.drivers.Talon;

public class Telescope implements Subsystem {
    private static Telescope instance;
    public static Telescope getInstance(TelescopeCommand tCommand) {
        if(instance == null) {
            return instance = new Telescope(tCommand);
        } else {
            return instance;
        }
    }

    private final Controls controls;
    private final TelescopeCommand tCommand;

    private Talon master;
    private DSolenoid left;
    private DSolenoid right;
    private DSolenoid pawl;

    private Telescope(TelescopeCommand tCommand) {
        controls = Controls.getInstance();
        this.tCommand = tCommand;
        master = ControllerFactory.masterTalon(ActuatorMap.telescopingMaster, false);
        ControllerFactory.slaveVictor(ActuatorMap.telescopingSlave, false, master);
        left = new DSolenoid(ActuatorMap.climberL);
        right = new DSolenoid(ActuatorMap.climberR);
        pawl = new DSolenoid(ActuatorMap.pawlRelease);

        master.setName("Telescope");
        left.setName("Climber Piston");
        pawl.setName("Pawl");
    }


    @Override
    public void update() {
        pawl.set(tCommand.isPawl());
        if(tCommand.isExtending()) {
            master.setOutput(1);
        } else if(tCommand.isRetracting()) {
            master.setOutput(-1);
        } else {
            master.setOutput(0);
        }

        if(tCommand.isTrenchMode()) {
            // Ooh yeah it's trench time
            left.set(false);
            right.set(false);
        } else {
            left.set(tCommand.isUp());
            right.set(tCommand.isUp());
        }
    }

    @Override
    public void reset() {
        master.setOutput(0);
        left.set(false);
        right.set(false);
    }
}
