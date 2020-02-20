package frc.robot.subsystems;

import frc.robot.commands.ShooterCommand;
import frc.util.*;
import frc.util.drivers.*;

public class Shooter implements Subsystem {
    private static Shooter instance;
    public static Shooter getInstance(ShooterCommand sCommand) {
        if(!Constants.shooterEnabled) {
            return null;
        } else if(instance == null) {
            return instance = new Shooter(sCommand);
        } else {
            return instance;
        }
    }

    private final ShooterCommand sCommand;
    private final Limelight limelight;
    private final Talon master;
    private final Talon hood;
    private final PD hoodActuator;

    private double hoodError = Double.MAX_VALUE;
    private boolean forceShoot = false;
    private boolean hoodConfigured = false;

    public Shooter(ShooterCommand sCommand) {
        this.sCommand = sCommand;
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

    public void aimHood(boolean aim, boolean trenchMode) {
        if(!hoodConfigured) {
            hood.setOutput(-.5);
            if(hood.isRevLimit()) {
                hood.zeroSensor();
                hoodConfigured = true;
            }
        } else if(trenchMode) {
            // Ooh yeah it's trench time
            if(Constants.startingHoodAngle + Conversion.encoderTicksToDegrees(hood.getSensor()) > Constants.trenchSafeHoodAngle) {
                hood.setOutput(-1);
            }
        } else if(aim) {
            double wantedAngle = Conversion.getHoodAngle(limelight.isValidTarget(), limelight.getDistance());
            double realAngle = Constants.startingHoodAngle + Conversion.encoderTicksToDegrees(hood.getSensor());
            hoodError = realAngle - wantedAngle;
            hood.setOutput(hoodActuator.getOutput(hoodError));
        } else {
            hood.setOutput(0);
        }
    }

    public double getRpm() {
        return Conversion.velocityToRpm(master.getVelocity());
    }

    public boolean isHoodAimed() {
        return hoodConfigured && Math.abs(hoodError) < Constants.hoodAllowedError;
    }

    @Override
    public void update() {
        // Run flywheel
        double output = sCommand.getOutput();
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
        aimHood(sCommand.isAimHood(), sCommand.isTrenchMode());
    }

    @Override
    public void reset() {
        master.setOutput(0);
        hoodConfigured = false;
    }
}
