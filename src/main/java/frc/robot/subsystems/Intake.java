package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import frc.robot.commands.IntakeCommand;
import frc.robot.states.IntakeState;
import frc.robot.states.RobotState;
import frc.util.ActuatorMap;
import frc.util.Constants;
import frc.util.drivers.ControllerFactory;
import frc.util.drivers.DSolenoid;

public class Intake implements Subsystem {
    private static Intake instance;
    public static Intake getInstance(IntakeCommand iCommand) {
        if(!Constants.intakeEnabled) {
            return null;
        } else if(instance == null) {
            return instance = new Intake(iCommand);
        } else {
            return instance;
        }
    }

    private IntakeCommand iCommand;
    private IntakeState state;
    private DSolenoid piston;
    private VictorSPX victor;

    public Intake(IntakeCommand iCommand) {
        this.iCommand = iCommand;
        state = RobotState.getInstance().getIntakeState();
        piston = new DSolenoid(ActuatorMap.intakeExtend, ActuatorMap.intakeRetract);
        piston.setName("Intake Sol");
        piston.set(false);
        victor = ControllerFactory.victor(ActuatorMap.intake, true);
    }

    @Override
    public void update() {
        piston.set(iCommand.piston());

        if(iCommand.exhaust()) {
            state.setRunning(false);
            victor.set(ControlMode.PercentOutput, -1);
        } else if(iCommand.intake()) {
            state.setRunning(true);
            victor.set(ControlMode.PercentOutput, .8);
        } else {
            state.setRunning(false);
            victor.set(ControlMode.PercentOutput, 0);
        }
    }

    @Override
    public void reset() {
        piston.set(false);
    }
}
