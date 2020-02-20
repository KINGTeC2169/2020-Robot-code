package frc.robot.auto.actions;

public class Parallel implements Action {
    private final Action[] actions;
    private boolean[] finished;
    private boolean stopped;
    private boolean and; // If true, task finishes once all sub-tasks finish. Otherwise, task finishes once any sub-task finishes

    public Parallel(Action... actions) {
        this(false, actions);
    }

    public Parallel(boolean and, Action... actions) {
        this.actions = actions;
        this.and = and;
        finished = new boolean[actions.length];
    }

    @Override
    public void start() {
        for(int i = 0; i < actions.length; i++) {
            actions[i].start();
            finished[i] = false;
        }
        stopped = false;
    }

    @Override
    public void run() {
        if(stopped) return;
        for(int i = 0; i < actions.length; i++) {
            if(!finished[i] && actions[i].isFinished()) {
                actions[i].stop();
                finished[i] = true;
            } else if(!finished[i]) {
                actions[i].run();
            }
        }

        if(isFinished()) stop();
    }

    @Override
    public void stop() {
        for(int i = 0; i < actions.length; i++) {
            if(!finished[i]) {
                actions[i].stop();
            }
        }
    }

    @Override
    public boolean isFinished() {
        boolean taskFinished = and;
        for(boolean f : finished) {
            if(and) {
                taskFinished &= f; // Imagine using the &= operator two years in a row
            } else {
                taskFinished |= f;
            }
        }
        if(taskFinished) {
            stopped = true;
        }
        return taskFinished;
    }
}
