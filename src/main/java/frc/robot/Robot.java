package frc.robot;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.auto.modes.Mode;
import frc.robot.auto.modes.TestMode;
import frc.robot.subsystems.Superstructure;
import frc.robot.util.ColorSensor;
import frc.robot.states.RobotState;

public class Robot extends TimedRobot {

    private Superstructure superstructure = Superstructure.getInstance();
    private RobotState state = RobotState.getInstance();
    private ColorSensor colorSensor = new ColorSensor();

    private Mode autoMode;
    private SendableChooser<Mode> chooser = new SendableChooser<Mode>();

    @Override
    public void robotInit() {
        chooser.setDefaultOption("Test Mode", new TestMode());
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
        autoMode = chooser.getSelected();
        if (autoMode != null) {
            autoMode.start();
        }
        autoMode = new TestMode();
    }

    @Override
    public void autonomousPeriodic() {
        autoMode.run();
    }

    @Override
    public void teleopInit() {
        superstructure.reset();
        RobotState.getInstance().reset();
        if (autoMode != null) {
            autoMode.stop();
        }

        NetworkTableInstance.getDefault().getTable("limelight").getEntry("ledMode").setNumber(3);
    }

    @Override
    public void teleopPeriodic() {
        Scheduler.getInstance().run();

        superstructure.update(state);

        colorSensor.update();
        SmartDashboard.putString("Sensor", colorSensor.toString());
    }

    @Override
    public void testInit() {
    }

    @Override
    public void testPeriodic() {
    }
}
