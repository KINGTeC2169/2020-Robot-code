package frc.robot.subsystems;

import frc.util.ActuatorMap;
import frc.util.Constants;
import frc.util.Controls;
import frc.util.drivers.ControllerFactory;
import frc.util.drivers.Talon;

public class Shooter implements Subsystem {
    private static Shooter instance;
    public static Shooter getInstance() {
        if(instance == null) {
            return instance = new Shooter();
        } else {
            return instance;
        }
    }

    Controls controls;
    Talon master;
    Talon slave;
    Talon hood;
    Talon indexer;

    public Shooter() {
        controls = Controls.getInstance();

        master = ControllerFactory.masterTalon(ActuatorMap.flywheelMaster, false);
        slave = ControllerFactory.slaveTalon(ActuatorMap.flywheelSlave, false, master);
        hood = ControllerFactory.masterTalon(ActuatorMap.flywheelHood, false);
        indexer = ControllerFactory.masterTalon(ActuatorMap.indexer, false);

        master.setName("Flywheel");
        hood.setName("Flywheel Hood");
        indexer.setName("Indexer");
    }

    @Override
    public void update() {
        double output = controls.xbox.getRawAxis(3);
        if(output > Constants.flywheelDeadband) {
            output = output > 1 - Constants.flywheelDeadband ? 1 : output;
            master.setOutput(output);
        } else {
            master.setOutput(0);
        }
    }

    @Override
    public void reset() {
        master.setOutput(0);
    }
}
