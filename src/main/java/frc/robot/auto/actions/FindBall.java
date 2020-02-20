package frc.robot.auto.actions;

import frc.util.BallTracker;

public class FindBall extends Ghost {
    private final BallTracker ballTracker;

    public FindBall() {
        ballTracker = BallTracker.getInstance();
    }

    @Override
    public boolean isFinished() {
        return ballTracker.canSeeBall();
    }
}
