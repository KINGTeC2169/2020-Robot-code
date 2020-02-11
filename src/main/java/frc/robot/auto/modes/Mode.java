package frc.robot.auto.modes;

public interface Mode {
    void start();
    void run();
    void stop();
    boolean isRunning();
}
