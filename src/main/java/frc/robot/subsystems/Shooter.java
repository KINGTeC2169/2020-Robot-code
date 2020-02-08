package frc.robot.subsystems;

import frc.util.*;
import frc.util.drivers.*;

public class Shooter implements Subsystem {
    private static Shooter instance;
    public static Shooter getInstance() {
        if(instance == null) {
            return instance = new Shooter();
        } else {
            return instance;
        }
    }

    private final Controls controls;
    private final Limelight limelight;
    private final Talon master;
    private final Talon hood;
    private final PD hoodActuator;

    private boolean forceShoot = false;

    public Shooter() {
        controls = Controls.getInstance();
        limelight = Limelight.getInstance();

        master = ControllerFactory.masterTalon(ActuatorMap.flywheelMaster, false);
        ControllerFactory.slaveTalon(ActuatorMap.flywheelSlave, false, master);
        hood = ControllerFactory.masterTalon(ActuatorMap.flywheelHood, false);

        master.setName("Flywheel");
        hood.setName("Flywheel Hood");

        hoodActuator = new PD(Constants.hoodActuationP, Constants.hoodActuationD);
    }

    public void forceShoot(boolean on) {
        forceShoot = on;
    }

    public double getRpm() {
        return Conversion.velocityToRpm(master.getVelocity());
    }

    @Override
    public void update() {
        // Run flywheel
        double output = controls.xbox.getRawAxis(3);
        if(forceShoot) {
            output = 1;
        }
        if(output > Constants.flywheelDeadband) {
            output = output > 1 - Constants.flywheelDeadband ? 1 : output;
            master.setOutput(output);
        } else {
            master.setOutput(0);
        }

        // Adjust hood
        double wantedAngle = Conversion.getHoodAngle(limelight.isValidTarget(), limelight.getDistance());
        double realAngle = Constants.startingHoodAngle + Conversion.encoderTicksToDegrees(hood.getSensor());
        hood.setOutput(hoodActuator.getOutput(wantedAngle - realAngle));
    }

    @Override
    public void reset() {
        master.setOutput(0);
    }
}
