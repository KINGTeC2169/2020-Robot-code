package frc.robot.auto.actions;

import frc.util.drivers.Limelight;

public class FindTarget extends Ghost {
    private final Limelight limelight;

    public FindTarget() {
        limelight = Limelight.getInstance();
    }

    @Override
    public boolean isFinished() {
        return limelight.isValidTarget();
    }
}
