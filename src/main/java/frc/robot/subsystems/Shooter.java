package frc.robot.subsystems;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
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

    private PD flywheelController;
    private double hoodError = Double.MAX_VALUE;
    private boolean forceShoot = false;
    private boolean hoodConfigured = false;

    public Shooter(ShooterCommand sCommand) {
        this.sCommand = sCommand;
        limelight = Limelight.getInstance();

        master = ControllerFactory.masterTalon(ActuatorMap.flywheelMaster, true);
        ControllerFactory.slaveVictor(ActuatorMap.flywheelSlave, false, master);
        hood = ControllerFactory.masterTalon(ActuatorMap.flywheelHood, true);

        master.setSensorPhase(true);
        hood.setSensorPhase(true);

        hood.setPID(Constants.hoodActuationP, Constants.hoodActuationI, Constants.hoodActuationD);
//        master.setPID(Constants.flywheelP, 0, Constants.flywheelD);
        flywheelController = new PD(Constants.flywheelP, Constants.flywheelD);

        master.setName("Flywheel");
        hood.setName("Flywheel Hood");
    }

    public void forceShoot(boolean on) {
        forceShoot = on;
    }

    public void aimHood(boolean aim, boolean trenchMode) {
        aim = true;
        if(!hoodConfigured) {
            hood.setOutput(-.5);
            if(hood.isRevLimit()) {
                hood.zeroSensor();
                hoodConfigured = true;
                DriverStation.reportError("Zeroed Hood!", false);
            }
        } else if(trenchMode) {
            // Ooh yeah it's trench time
            if(Constants.startingHoodAngle + Conversion.encoderTicksToDegrees(hood.getSensor()) > Constants.trenchSafeHoodAngle) {
                hood.setOutput(-1);
            }
        } else if(aim) {
//            double wantedAngle = Conversion.getHoodAngle(limelight.isValidTarget(), limelight.getDistance());
            if(!SmartDashboard.containsKey("Get Wanted Angle")) SmartDashboard.putNumber("Get Wanted Angle", 45);
            double ty = limelight.getCenter().y;
//            double wantedAngle = 0.159 * ty * ty - 9.499 * ty + 181.02;
//            double wantedAngle = SmartDashboard.getNumber("Get Wanted Angle", 45);
            double wantedAngle = sCommand.getWantedAngle();
            double realAngle = Constants.startingHoodAngle - hood.getSensor() / Constants.ticksPerHoodDegree;
            SmartDashboard.putNumber("Hood encoder", hood.getSensor());
            SmartDashboard.putNumber("Wanted Hood encoder", (Constants.startingHoodAngle - wantedAngle) * Constants.ticksPerHoodDegree);
            SmartDashboard.putNumber("Wanted angle", wantedAngle);
            SmartDashboard.putNumber("Real angle", realAngle);
            hoodError = realAngle - wantedAngle;

            hood.setDesiredPosition((Constants.startingHoodAngle - wantedAngle) * Constants.ticksPerHoodDegree);
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

    private double flywheelBase = Constants.flywheelBase;
    @Override
    public void update() {
        // Run flywheel
        if(sCommand.isShooting() || forceShoot) {
            double error = Constants.desiredShootingRpm - getRpm();
            if(error < 500) {
                flywheelBase += .00002 * error;
                SmartDashboard.putNumber("Flywheel base", flywheelBase);
            } else {
                flywheelBase = Constants.flywheelBase;
            }
            master.setOutput(flywheelBase + flywheelController.getOutput(error));
//            master.setEncoderVelocity(Conversion.rpmToVelocity(Constants.desiredShootingRpm));
        } else {
            master.setOutput(0);
        }

        SmartDashboard.putNumber("Hood Error", hood.talon.getClosedLoopError());

        SmartDashboard.putNumber("RPM", getRpm());

        // Adjust hood
        aimHood(sCommand.isAimHood(), sCommand.isTrenchMode());
    }

    @Override
    public void reset() {
        master.setOutput(0);
        hoodConfigured = false;
    }
}
