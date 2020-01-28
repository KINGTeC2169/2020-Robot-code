package frc.robot;

import edu.wpi.first.wpilibj.RobotBase;
import org.opencv.videoio.VideoCapture;
import frc.robot.vision.CameraServer;

public final class Main {
  private Main() {
  }

  public static void main(String... args) {
    //new Thread(new CameraServer(), "Vision Thread").start();
    RobotBase.startRobot(Robot::new);
  }
}
