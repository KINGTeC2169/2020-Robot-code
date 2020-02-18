package frc.robot;

import edu.wpi.first.wpilibj.RobotBase;
import frc.util.BallTracker;

public final class Main {
  private Main() {
  }

  public static void main(String... args) {
    Thread thread = new Thread(BallTracker::spawnThread);
    thread.start();
    RobotBase.startRobot(Robot::new);
  }
}
