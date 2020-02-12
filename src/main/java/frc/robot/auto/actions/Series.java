package frc.robot.auto.actions;

public class Series implements Action {
    private final Action first;
    private final Action second;

    private boolean firstFinished;
    private boolean secondFinished;
    private boolean stopped;

    public Series(Action first, Action second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public void start() {
        first.start();
        firstFinished = false;
        secondFinished = false;
    }

    @Override
    public void run() {
        if(stopped) return;
        if(!firstFinished && first.isFinished()) {
            System.out.println("A");
            firstFinished = true;
            first.stop();
            second.start();
            second.run();
        } else if(!firstFinished) {
            System.out.println("B");
            first.run();
        } else if(second.isFinished()) {
            System.out.println("C");
            secondFinished = true;
            second.stop();
            stopped = true;
        } else if(!secondFinished) {
            System.out.println("D");
            second.run();
        }
    }

    @Override
    public void stop() {
        if(!firstFinished) {
            first.stop();
        } else {
            second.stop();
        }
    }

    @Override
    public boolean isFinished() {
        return firstFinished && secondFinished;
    }
}
