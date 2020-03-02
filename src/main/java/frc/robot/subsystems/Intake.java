package frc.robot.subsystems;

import frc.robot.commands.IntakeCommand;
import frc.robot.states.IntakeState;
import frc.robot.states.RobotState;
import frc.util.ActuatorMap;
import frc.util.Constants;
import frc.util.drivers.ControllerFactory;
import frc.util.drivers.Sol;
import frc.util.drivers.Victor;

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
    private Sol piston;
    private Victor victor;

    public Intake(IntakeCommand iCommand) {
        this.iCommand = iCommand;
        state = RobotState.getInstance().getIntakeState();
        piston = new Sol(ActuatorMap.intakeSol);
        piston.setName("Intake Sol");
        piston.set(false);
        victor = ControllerFactory.victor(ActuatorMap.intake, true);
        victor.setName("Intake");
    }

    @Override
    public void update() {
        piston.set(iCommand.piston());

        if(iCommand.exhaust()) {
            state.setRunning(false);
            victor.setOutput(-1);
        } else if(iCommand.intake()) {
            state.setRunning(true);
            victor.setOutput(.8);
        } else {
            state.setRunning(false);
            victor.setOutput(0);
        }
    }

    @Override
    public void reset() {
        piston.set(false);
    }
}
