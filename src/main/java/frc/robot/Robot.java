package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.auto.actions.DoNothing;
import frc.robot.subsystems.Superstructure;
import frc.robot.util.ColorSensor;

public class Robot extends TimedRobot {

    private Superstructure superstructure = new Superstructure();
    private ColorSensor colorSensor = new ColorSensor();

    private Command autoCommand;
    private SendableChooser<Command> chooser = new SendableChooser<>();

    @Override
    public void robotInit() {
        chooser.setDefaultOption("Autonomous Command", new DoNothing());
        SmartDashboard.putData("Auto mode", chooser);
    }

    @Override
    public void robotPeriodic() {
    }

    @Override
    public void disabledInit(){
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

        superstructure.handle();

        colorSensor.handle();
        SmartDashboard.putString("Sensor", colorSensor.toString());
    }

    @Override
    public void testInit() {
    }

    @Override
    public void testPeriodic() {
    }
}
