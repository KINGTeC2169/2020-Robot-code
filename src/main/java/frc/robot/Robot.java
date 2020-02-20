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
import frc.util.BallTracker;
import frc.util.Debug;

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
        autoMode = new TestMode(superstructure, commandMachine);
    }

    @Override
    public void robotPeriodic() {
    }

    @Override
    public void disabledInit(){
        superstructure.reset();
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
