package frc.robot;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import frc.robot.auto.modes.Mode;
import frc.robot.auto.modes.Owen;
import frc.robot.auto.modes.TestMode;
import frc.robot.commands.CommandMachine;
import frc.robot.subsystems.Superstructure;
import frc.robot.states.RobotState;
import frc.util.Debug;

public class Robot extends TimedRobot {

    private CommandMachine commandMachine = CommandMachine.getInstance();
    private Superstructure superstructure = Superstructure.getInstance();
    private RobotState state = RobotState.getInstance();

    // Auto stuff
    private Mode autoMode;
    private SendableChooser<Mode> chooser = new SendableChooser<Mode>();

    @Override
    public void robotInit() {
        Debug.start();
        superstructure.start();
        autoMode = new Owen(superstructure, commandMachine);
    }

    @Override
    public void robotPeriodic() {
    }

    @Override
    public void disabledInit(){
        superstructure.reset();
    }

    @Override
    public void disabledPeriodic() {
    }

    @Override
    public void autonomousInit() {
        Debug.init();
        superstructure.reset();
        state.reset();
        if (autoMode != null) {
            autoMode.start();
        }
    }

    @Override
    public void autonomousPeriodic() {
        superstructure.update(state);
        state.update();
        autoMode.run();
    }

    @Override
    public void teleopInit() {
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
