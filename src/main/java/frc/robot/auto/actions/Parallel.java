package frc.robot.auto.actions;

public class Parallel implements Action {
    private final Action a;
    private final Action b;

    private boolean aFinished;
    private boolean bFinished;
    private boolean stopped;

    public Parallel(Action a, Action b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public void start() {
        a.start();
        b.start();
        aFinished = false;
        bFinished = false;
        stopped = false;
    }

    @Override
    public void run() {
        if(!aFinished && a.isFinished()) {
            a.stop();
            aFinished = true;
        } else if(!aFinished) {
            a.run();
        }

        if(!bFinished && b.isFinished()) {
            b.stop();
            bFinished = true;
        } else if(!bFinished) {
            b.run();
        }
    }

    @Override
    public void stop() {
        if(!aFinished) {
            a.stop();
        }
        if(!bFinished){
            b.stop();
        }
    }

    @Override
    public boolean isFinished() {
        return aFinished && bFinished;
    }
}
