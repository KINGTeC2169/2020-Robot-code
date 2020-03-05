package frc.robot.auto.actions;

public class Series implements Action {
    private final Action[] actions;
    private int idx = 0;
    private boolean stopped;

    public Series(Action... actions) {
        this.actions = actions;
    }

    @Override
    public void start() {
        if(actions.length > 0) {
            actions[0].start();
        }
        idx = 0;
    }

    @Override
    public void run() {
        if(stopped) return;
        if(idx < actions.length && actions[idx].isFinished()) {
            actions[idx].stop();
            idx++;
            if(idx < actions.length) {
                actions[idx].start();
            }
        }
        if(idx < actions.length) {
            actions[idx].run();
        }

        if(isFinished()) stop();
    }

    @Override
    public void stop() {
        for (int i = idx; i < actions.length; i++) {
            actions[i].stop();
        }
    }

    @Override
    public boolean isFinished() {
        if(idx >= actions.length) {
            return stopped = true;
        } else {
            return false;
        }
    }
}
