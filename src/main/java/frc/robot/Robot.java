package frc.robot;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.auto.modes.*;
import frc.robot.commands.CommandMachine;
import frc.robot.subsystems.Superstructure;
import frc.robot.states.RobotState;
import frc.util.BallTracker;

public class Robot extends TimedRobot {

    private final CommandMachine commandMachine = CommandMachine.getInstance();
    private final Superstructure superstructure = Superstructure.getInstance();
    private final RobotState state = RobotState.getInstance();
    private final BallTracker ballTracker = BallTracker.getInstance();

    // Auto stuff
    private Mode autoMode;
    private SendableChooser<Mode> chooser = new SendableChooser<Mode>();

    @Override
    public void robotInit() {
        superstructure.start();

        SmartDashboard.putData("Auto Chooser", chooser);
        chooser.addOption("Owen", new Owen());
        chooser.addOption("Test", new TestMode(0));
        chooser.addOption("Verdun", new Verdun());

        SmartDashboard.putNumber("Test Mode", 0);
    }

    @Override
    public void robotPeriodic() {
    }

    @Override
    public void disabledInit(){
        superstructure.reset();
        superstructure.stop();
        commandMachine.reset();
        if (autoMode != null) {
            autoMode.stop();
        }
    }

    @Override
    public void disabledPeriodic() {
    }

    @Override
    public void autonomousInit() {
        superstructure.reset();
        ballTracker.enable();
        state.reset();
        autoMode = chooser.getSelected();
        if (autoMode != null) {
            if(autoMode instanceof TestMode) {
                autoMode = new TestMode((int) SmartDashboard.getNumber("Test Mode", 0));
            }
            autoMode.start();
        }
    }

    @Override
    public void autonomousPeriodic() {
        superstructure.update(state);
        state.update();
        if(autoMode != null) {
            autoMode.run();
        }
    }

    @Override
    public void teleopInit() {
        commandMachine.reset();
        state.reset();
        if (autoMode != null && autoMode.isRunning()) {
            autoMode.stop();
        }

        NetworkTableInstance.getDefault().getTable("limelight").getEntry("ledMode").setNumber(3);
    }

    @Override
    public void teleopPeriodic() {
        commandMachine.teleop();
        state.update();
        superstructure.update(state);
    }

    @Override
    public void testInit() {
    }

    @Override
    public void testPeriodic() {
    }
}
