package frc.util.drivers;

import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import frc.util.Constants;
import frc.util.Debug;

public class Talon {
    public TalonSRX talon;

    private final boolean testing;
    private String name;
    private int id;
    private int sensorTicks = Integer.MIN_VALUE;
    private double output = 0;

    public Talon(int id) {
        this.id = id;
        testing = Constants.usingTestBed;
        if(!testing) {
            talon = new TalonSRX(id);
            configTalon();
        }
    }

    protected int getId() {
        return id;
    }

    protected void setInverted(boolean invert) {
        if(!testing) {
            talon.setInverted(invert);
        }
    }

    protected void makeFollower(Talon master) {
        if(!testing) {
            talon.set(ControlMode.Follower, master.getId());
        }
    }

    public void reset() {
        setOutput(0);
    }

    public void configSensor() {
        if(!testing) {
            talon.configSelectedFeedbackSensor(TalonSRXFeedbackDevice.QuadEncoder, 0, Constants.talonTimeoutMs);
        }
    }

    public void setSensorPhase(boolean sensorPhase) {
        if(!testing) {
            talon.setSensorPhase(sensorPhase);
        }
    }

    public void zeroSensor() {
        if(!testing) {
            talon.setSelectedSensorPosition(0, 0, Constants.talonTimeoutMs);
        } else {
            if(sensorTicks != Integer.MIN_VALUE) {
                sensorTicks = 0;
            }
            Debug.putNumber(name + " encoder value", 0);
        }
    }

    public void setOutput(double output) {
        this.output = output;
        if(name != null) {
            Debug.putNumber(name, output);
        }
        if(!testing) {
            talon.set(ControlMode.PercentOutput, output);
        }
    }

    public double getOutput() {
        return output;
    }

    public double getSensor() {
        if(testing && name != null) {
            if(sensorTicks == Integer.MIN_VALUE) {
                return Debug.getNumber(name + " encoder value");
            } else {
                Debug.putNumber(name + " encoder value", sensorTicks);
                return sensorTicks;
            }
        } else if(testing) {
            return 0;
        }

        double ticks = talon.getSelectedSensorPosition(0);
        Debug.putNumber(name + " encoder value", ticks);
        return ticks;
    }

    public double getVelocity() {
        if(testing && name != null) {
            return Debug.getNumber(name + " encoder vel.");
        } else if(testing) {
            return 0;
        }

        double vel = talon.getSelectedSensorVelocity(0);
        Debug.putNumber(name + " encoder vel.", vel);
        return vel;
    }

    public void changeSensor(int ticks) {
        if(sensorTicks == Integer.MIN_VALUE) {
            sensorTicks = 0;
        }
        sensorTicks += ticks;
    }

    public boolean isFwdLimit() {
        if(testing) {
            return Debug.getBoolean(name + " fwd limit");
        } else {
            boolean fwdLimit = talon.isFwdLimitSwitchClosed() == 1;
            Debug.putBoolean(name + " fwd limit", fwdLimit);
            return fwdLimit;
        }
    }

    public boolean isRevLimit() {
        if(testing) {
            return Debug.getBoolean(name + " rev limit");
        } else {
            boolean revLimit = talon.isRevLimitSwitchClosed() == 1;
            Debug.putBoolean(name + " fwd limit", revLimit);
            return revLimit;
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    private void configTalon() {
        talon.set(ControlMode.PercentOutput, 0.0);

        /*
        talon.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, Constants.talonTimeoutMs);
        talon.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, Constants.talonTimeoutMs);
        talon.overrideLimitSwitchesEnable(false);
         */

        talon.setNeutralMode(NeutralMode.Brake);

        /*
        talon.configSetParameter(ParamEnum.eClearPositionOnLimitF, 0, 0, 0, Constants.talonTimeoutMs);
        talon.configSetParameter(ParamEnum.eClearPositionOnLimitR, 0, 0, 0, Constants.talonTimeoutMs);

        talon.configNominalOutputForward(0, Constants.talonTimeoutMs);
        talon.configNominalOutputReverse(0, Constants.talonTimeoutMs);
        talon.configNeutralDeadband(0, Constants.talonTimeoutMs);

        talon.configPeakOutputForward(1.0, Constants.talonTimeoutMs);
        talon.configPeakOutputReverse(-1.0, Constants.talonTimeoutMs);

        talon.selectProfileSlot(0, 0);

        talon.configVelocityMeasurementPeriod(config.VELOCITY_MEASUREMENT_PERIOD, Constants.talonTimeoutMs);
        talon.configVelocityMeasurementWindow(config.VELOCITY_MEASUREMENT_ROLLING_AVERAGE_WINDOW, Constants.talonTimeoutMs);

        talon.configOpenloopRamp(config.OPEN_LOOP_RAMP_RATE, Constants.talonTimeoutMs);
        talon.configClosedloopRamp(config.CLOSED_LOOP_RAMP_RATE, Constants.talonTimeoutMs);

        talon.configVoltageCompSaturation(0.0, Constants.talonTimeoutMs);
        talon.configVoltageMeasurementFilter(32, Constants.talonTimeoutMs);
        talon.enableVoltageCompensation(false);

        talon.enableCurrentLimit(config.ENABLE_CURRENT_LIMIT);

        talon.setStatusFramePeriod(StatusFrameEnhanced.Status_1_General, config.GENERAL_STATUS_FRAME_RATE_MS, Constants.talonTimeoutMs);
        talon.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, config.FEEDBACK_STATUS_FRAME_RATE_MS, Constants.talonTimeoutMs);

        talon.setStatusFramePeriod(StatusFrameEnhanced.Status_3_Quadrature, config.QUAD_ENCODER_STATUS_FRAME_RATE_MS, Constants.talonTimeoutMs);
        talon.setStatusFramePeriod(StatusFrameEnhanced.Status_4_AinTempVbat, config.ANALOG_TEMP_VBAT_STATUS_FRAME_RATE_MS, Constants.talonTimeoutMs);
        talon.setStatusFramePeriod(StatusFrameEnhanced.Status_8_PulseWidth, config.PULSE_WIDTH_STATUS_FRAME_RATE_MS, Constants.talonTimeoutMs);

        talon.setControlFramePeriod(ControlFrame.Control_3_General, config.CONTROL_FRAME_PERIOD_MS);

        */

        talon.configContinuousCurrentLimit(40);
        talon.enableCurrentLimit(true);
    }

    public void setPID(final double p, final double i, final double d) {
        talon.config_kP(0, p);
        talon.config_kI(0, i);
        talon.config_kD(0, d);
    }

    public void setDesiredPosition(double position) {
        talon.set(ControlMode.Position, position);
        if(name != null) {
            Debug.putNumber(name, talon.getMotorOutputPercent());
        }
    }

    public void setEncoderVelocity(double velocity) {
        talon.set(ControlMode.Velocity, velocity);
        if(name != null) {
            Debug.putNumber(name + " encoder vel.", velocity);
        }
    }
}
