package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
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
    private final TalonSRX master;
    private final TalonSRX hood;

    private PD flywheelController;
    private double hoodError = Double.MAX_VALUE;
    private double flywheelBase = Constants.flywheelBase;
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

        hood.config_kP(0, Constants.hoodActuationP, Constants.talonTimeoutMs);
        hood.config_kI(0, Constants.hoodActuationI, Constants.talonTimeoutMs);
        hood.config_kD(0, Constants.hoodActuationD, Constants.talonTimeoutMs);
        flywheelController = new PD(Constants.flywheelP, Constants.flywheelD);
    }

    public void forceShoot(boolean on) {
        forceShoot = on;
    }

    public void aimHood(boolean aim, boolean trenchMode) {
        if(hood.isFwdLimitSwitchClosed() == 1) {
            hoodConfigured = false; // Recalibrate if we hit the forward limit switch
        }
        if(!hoodConfigured) {
            // Calibrate the hood
            hood.set(ControlMode.PercentOutput, -.5);
            if(hood.isRevLimitSwitchClosed() == 1) {
                hood.getSelectedSensorPosition(0);
                hoodConfigured = true;
            }
        } else if(trenchMode) {
            // Ooh yeah it's trench time
            if(Constants.startingHoodAngle + Conversion.encoderTicksToDegrees(hood.getSelectedSensorPosition(0)) > Constants.trenchSafeHoodAngle) {
                hood.set(ControlMode.PercentOutput, -1);
            }
        } else if(aim) {
            double wantedAngle;
            if(Constants.manualHoodControl) {
                wantedAngle = sCommand.getWantedAngle();
            } else {
                wantedAngle = Conversion.getDesiredHoodAngle(limelight.isValidTarget(), limelight.getCenter().y);
            }
            double realAngle = Constants.startingHoodAngle - hood.getSelectedSensorPosition() / Constants.ticksPerHoodDegree;

//            SmartDashboard.putNumber("Hood encoder", hood.getSelectedSensorPosition());
//            SmartDashboard.putNumber("Wanted Hood encoder", (Constants.startingHoodAngle - wantedAngle) * Constants.ticksPerHoodDegree);
            SmartDashboard.putNumber("Wanted angle", wantedAngle);
            SmartDashboard.putNumber("Real angle", realAngle);

            hoodError = realAngle - wantedAngle;
            hood.set(ControlMode.Position, (Constants.startingHoodAngle - wantedAngle) * Constants.ticksPerHoodDegree);
        } else {
            hood.set(ControlMode.PercentOutput, 0);
        }
    }

    public double getRpm() {
        return Conversion.velocityToRpm(master.getSelectedSensorVelocity(0));
    }

    public double getRpmError() {
        return Conversion.getDesiredRpm(limelight.isValidTarget(), limelight.getCenter().y) - getRpm();
    }

    public boolean isHoodAimed() {
        return hoodConfigured && Math.abs(hoodError) < Constants.hoodAllowedError;
    }

    @Override
    public void update() {
        // Run flywheel
        if(sCommand.isShooting() || forceShoot) {
            double error = getRpmError();
            if(error < 500) {
                flywheelBase += Constants.flywheelBaseP * error;
                SmartDashboard.putNumber("Flywheel base", flywheelBase);
            } else {
                flywheelBase = Constants.flywheelBase;
            }
            master.set(ControlMode.PercentOutput, flywheelBase + flywheelController.getOutput(error));
        } else {
            master.set(ControlMode.PercentOutput, 0);
        }

        SmartDashboard.putNumber("Hood Error", hood.getClosedLoopError());

        SmartDashboard.putNumber("RPM", getRpm());

        // Adjust hood
        aimHood(sCommand.isAimHood(), sCommand.isTrenchMode());
    }

    @Override
    public void reset() {
        master.set(ControlMode.PercentOutput, 0);
        hoodConfigured = false;
    }
}
