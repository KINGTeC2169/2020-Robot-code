package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.commands.ShooterCommand;
import frc.util.*;
import frc.util.drivers.*;

public class Shooter implements Subsystem {
    private static Shooter instance;
    private boolean isSpinning;
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

        master.configContinuousCurrentLimit(40);
        master.configClosedloopRamp(.03,0);
        master.enableCurrentLimit(true);

        master.setSensorPhase(true);
        hood.setSensorPhase(true);

        hood.config_kP(0, Constants.hoodActuationP, Constants.talonTimeoutMs);
        hood.config_kI(0, Constants.hoodActuationI, Constants.talonTimeoutMs);
        hood.config_kD(0, Constants.hoodActuationD, Constants.talonTimeoutMs);
        hood.config_kF(0,Constants.hoodActuationF,Constants.talonTimeoutMs);
        hood.config_IntegralZone(0,(int)(8192/Constants.hoodActuationP));
        flywheelController = new PD(Constants.flywheelP, Constants.flywheelD);

        SmartDashboard.putBoolean("Primed?", false);

    }

    public boolean isSpinningUp(){
        return isSpinning;
    }

    public void forceShoot(boolean on) {
        forceShoot = on;
    }

    public void aimHood(boolean aim, boolean trenchMode) {
        double wantedAngle;
        if(Constants.manualHoodControl) {
            wantedAngle = sCommand.getWantedAngle();
        } else {
            wantedAngle = Conversion.getDesiredHoodAngle(limelight.isValidTarget(), limelight.getCenter().y);
        }
        SmartDashboard.putNumber("Hood Target Angle", wantedAngle);

        if(hood.isFwdLimitSwitchClosed() == 1) {
            hoodConfigured = false; // Recalibrate if we hit the forward limit switch
//            System.out.println("Fwd Limit Hit!");
        }
        if(!hoodConfigured) {
//            SmartDashboard.putNumber("Hood State", 1);
//            System.out.println("Configuring Hood!");
            // Calibrate the hood
            hood.set(ControlMode.PercentOutput, -.5);
            if(hood.isRevLimitSwitchClosed() == 1) {
                hood.getSelectedSensorPosition(0);
                hoodConfigured = true;
            }
        } else if(trenchMode) {
//            SmartDashboard.putNumber("Hood State", 2);
            // Ooh yeah it's trench time
            if(Constants.startingHoodAngle + Conversion.encoderTicksToDegrees(hood.getSelectedSensorPosition(0)) > Constants.trenchSafeHoodAngle) {
                hood.set(ControlMode.PercentOutput, -1);
            }
        } else if(aim) {
//            SmartDashboard.putNumber("Hood State", 3);
            double realAngle = Constants.startingHoodAngle - hood.getSelectedSensorPosition() / Constants.ticksPerHoodDegree;

//            SmartDashboard.putNumber("Hood encoder", hood.getSelectedSensorPosition());
//            SmartDashboard.putNumber("Wanted Hood encoder", (Constants.startingHoodAngle - wantedAngle) * Constants.ticksPerHoodDegree);
            SmartDashboard.putNumber("Current Hood Angle", realAngle);

            hoodError = realAngle - wantedAngle;
            hood.set(ControlMode.Position, (Constants.startingHoodAngle - wantedAngle) * Constants.ticksPerHoodDegree);
        } else {
//            SmartDashboard.putNumber("Hood State", 4);
            hood.set(ControlMode.PercentOutput, -.5);
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
            isSpinning = true;
            if(Conversion.getDesiredRpm(limelight.isValidTarget(), limelight.getCenter().y) < 5000){
                double error = getRpmError();
                if(error < 500) {
                    flywheelBase += Constants.flywheelBaseP * error;
//                    SmartDashboard.putNumber("Flywheel base", flywheelBase);
                } else {
                    flywheelBase = Constants.flywheelBase;
                }
                master.set(ControlMode.PercentOutput, flywheelBase + flywheelController.getOutput(error));
            }
            else{
                double error = getRpmError();
                if(error < 500) {
                    flywheelBase += (Constants.flywheelBaseP + .00003) * error;
//                    SmartDashboard.putNumber("Flywheel base", flywheelBase);
                } else {
                    flywheelBase = Constants.flywheelBase + .25;
                }
                master.set(ControlMode.PercentOutput, flywheelBase + flywheelController.getOutput(error));
            }

            SmartDashboard.putBoolean("Primed?", Math.abs(getRpmError()) < 50);

        } else {
            SmartDashboard.putBoolean("Primed?", false);
            isSpinning = false;
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
