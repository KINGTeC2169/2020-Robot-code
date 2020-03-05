package frc.robot.auto.actions;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.commands.CommandMachine;
import frc.robot.commands.DriveCommand;
import frc.util.Constants;
import frc.util.drivers.Limelight;

public class AimAtTarget implements Action {
    private SearchTarget searchTarget;

    private final DriveCommand dCommand;
    private final Limelight limelight;

    private final int minimumAimedCycles = 10;
    private int aimedCycles = 0;

    public AimAtTarget() {
        dCommand = CommandMachine.getInstance().getDriveCommand();
        limelight = Limelight.getInstance();
    }

    private void lookAtTarget() {
        if(searchTarget == null) {
            searchTarget = new SearchTarget();
            searchTarget.start();
        }
        searchTarget.run();
    }

    @Override
    public void start() {

    }

    @Override
    public void run() {
        if(limelight.isValidTarget()) {
            if(searchTarget != null) {
                searchTarget.stop();
                searchTarget = null;
            }
            dCommand.setAutoVision(0);
        } else {
            lookAtTarget();
        }
    }

    @Override
    public void stop() {
        if(searchTarget != null) searchTarget.stop();
        dCommand.rest();
    }

    @Override
    public boolean isFinished() {
//        SmartDashboard.putNumber("aimed cycles", aimedCycles);
        if(limelight.isValidTarget() && Math.abs(limelight.getCenter().x) < Constants.acceptedAimError) {
            aimedCycles++;
        } else {
            aimedCycles = 0;
        }
        if(aimedCycles >= minimumAimedCycles) {
            return true;
        } else {
            return false;
        }
    }
}
