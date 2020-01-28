package frc.robot.auto.actions;

public interface Action {
    public void start();
    public void run();
    public void stop();
    public boolean isFinished();
}
