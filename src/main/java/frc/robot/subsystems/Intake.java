package frc.robot.subsystems;

import frc.robot.commands.IntakeCommand;
import frc.robot.states.IntakeState;
import frc.robot.states.RobotState;
import frc.util.ActuatorMap;
import frc.util.Constants;
import frc.util.drivers.ControllerFactory;
import frc.util.drivers.DSolenoid;
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
    private DSolenoid lsol;
    private DSolenoid rsol;
    private Victor victor;
    private boolean solenoidState = true;

    public Intake(IntakeCommand iCommand) {
        this.iCommand = iCommand;
        state = RobotState.getInstance().getIntakeState();
        lsol = new DSolenoid(ActuatorMap.intakeL);
        rsol = new DSolenoid(ActuatorMap.intakeR);
        lsol.setName("Intake Sol");
        lsol.set(true);
        rsol.set(true);
        victor = ControllerFactory.victor(ActuatorMap.intake, false);
        victor.setName("Intake");
    }

    @Override
    public void update() {
        lsol.set(iCommand.piston());
        rsol.set(iCommand.piston());

        if(iCommand.exhaust()) {
            state.setRunning(false);
            victor.setOutput(-1);
        } else if(iCommand.intake()) {
            state.setRunning(true);
            victor.setOutput(1);
        } else {
            state.setRunning(false);
            victor.setOutput(0);
        }
    }

    @Override
    public void reset() {
        lsol.set(true);
        rsol.set(true);
    }
}
