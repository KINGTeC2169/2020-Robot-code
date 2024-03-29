package frc.robot;

import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import frc.util.BallTracker;
import frc.util.Debug;

public final class Main {
  private Main() {
  }

  public static void main(String... args) {
    Thread ballTrackerThread = new Thread(BallTracker::spawnThread);
    ballTrackerThread.start();
    Thread debugThread = new Thread(Debug::spawnThread);
    debugThread.start();
    RobotBase.startRobot(Robot::new);

    //disable telemetry to stop multithreading error
    //LiveWindow.disableAllTelemetry();
  }
}
