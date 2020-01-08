package com.kingtec.frc2020;

import com.kingtec.frc2020.auto.actions.DoNothing;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;

import com.revrobotics.ColorSensorV3;

public class Robot extends TimedRobot {

    private ColorSensorV3 colorSensor = new ColorSensorV3(I2C.Port.kOnboard);

    private Command autoCommand;
    private SendableChooser<Command> chooser = new SendableChooser<>();

    @Override
    public void robotInit() {
        chooser.setDefaultOption("Autonomous Command", new DoNothing());
        SmartDashboard.putData("Auto mode", chooser);
    }

    @Override
    public void disabledInit(){

    }

    @Override
    public void robotPeriodic() {
        Color detected = colorSensor.getColor();
        double IR = colorSensor.getIR();

        SmartDashboard.putNumber("Red", detected.red);
        SmartDashboard.putNumber("Green", detected.green);
        SmartDashboard.putNumber("Blue", detected.blue);
        SmartDashboard.putNumber("IR", IR);

        int proximity = colorSensor.getProximity();
        SmartDashboard.putNumber("Proximity", proximity);
    }

    @Override
    public void disabledPeriodic() {
        Scheduler.getInstance().run();
    }

    @Override
    public void autonomousInit() {
        autoCommand = chooser.getSelected();
        if (autoCommand != null) {
            autoCommand.start();
        }
    }

    @Override
    public void autonomousPeriodic() {
        Scheduler.getInstance().run();
    }

    @Override
    public void teleopInit() {
        if (autoCommand != null) {
            autoCommand.cancel();
        }
    }

    @Override
    public void teleopPeriodic() {
        Scheduler.getInstance().run();
    }
}
