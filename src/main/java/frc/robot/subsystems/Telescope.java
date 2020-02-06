package frc.robot.subsystems;

import frc.robot.states.RobotState;
import frc.robot.states.TelescopeState;
import frc.util.ActuatorMap;
import frc.util.drivers.ControllerFactory;
import frc.util.drivers.DSolenoid;
import frc.util.drivers.Talon;

// TODO: Make this class actually work

public class Telescope implements Subsystem {
    private static Telescope instance;
    public static Telescope getInstance() {
        if(instance == null) {
            return instance = new Telescope();
        } else {
            return instance;
        }
    }

    private TelescopeState state;
    private Talon master;
    private DSolenoid left;
    private DSolenoid right;

    public Telescope() {
        state = RobotState.getInstance().getTelescopeState();
        master = ControllerFactory.masterTalon(ActuatorMap.telescopingMaster, false);
        ControllerFactory.slaveVictor(ActuatorMap.telescopingSlave, false, master);
        left = new DSolenoid(ActuatorMap.climberL);
        right = new DSolenoid(ActuatorMap.climberR);

        master.setName("Telescoping Master");
        left.setName("Climber Piston");
    }


    @Override
    public void update() {
        if(state.isExtending()) {
            master.setOutput(1);
        } else if(state.isRetracting()) {
            master.setOutput(-1);
        } else {
            master.setOutput(0);
        }

        left.set(state.isUp());
        right.set(state.isUp());
    }

    @Override
    public void reset() {
        master.setOutput(0);
        left.set(false);
        right.set(false);
    }
}
