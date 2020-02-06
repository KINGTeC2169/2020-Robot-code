package frc.robot.subsystems;

import frc.util.ActuatorMap;
import frc.util.Controls;
import frc.util.drivers.ControllerFactory;
import frc.util.drivers.DSolenoid;
import frc.util.drivers.Victor;

public class Intake implements Subsystem {
    private static Intake instance;
    public static Intake getInstance() {
        if(instance == null) {
            return instance = new Intake();
        } else {
            return instance;
        }
    }

    private Controls controls;
    private DSolenoid lsol;
    private DSolenoid rsol;
    private Victor victor;

    public Intake() {
        controls = new Controls();
        lsol = new DSolenoid(ActuatorMap.intakeL);
        rsol = new DSolenoid(ActuatorMap.intakeR);
        lsol.setName("Left Intake Sol");
        rsol.setName("Right Intake Sol");
        lsol.set(true);
        rsol.set(true);
        victor = ControllerFactory.victor(ActuatorMap.intake, false);
        victor.setName("Intake");
    }

    @Override
    public void update() {
        if(controls.xboxYPressed()) {
            lsol.set(!lsol.get());
            rsol.set(!rsol.get()); // It's toggle time
        }

        if(controls.rightButton(1)) {
            victor.setOutput(1);
        }
        if(controls.rightButton(2)) {
            victor.setOutput(-1);
        }
    }

    @Override
    public void reset() {
        lsol.set(true);
        rsol.set(true);
    }
}
