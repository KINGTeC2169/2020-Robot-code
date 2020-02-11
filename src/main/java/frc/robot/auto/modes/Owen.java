package frc.robot.auto.modes;

import frc.robot.auto.actions.LinearDrive;
import frc.robot.auto.actions.ShootBalls;

public class Owen implements Mode {
    /*
    * Shoot
    * Drive past line
    * */

    private ShootBalls shoot;
    private LinearDrive linearDrive;
    private boolean doneShooting = false;

    public Owen() {
        shoot = new ShootBalls();
        linearDrive = new LinearDrive(48);
    }

    @Override
    public void start() {
        shoot.start();
        doneShooting = false;
    }

    @Override
    public void run() {
        if(shoot.isFinished()) {
            doneShooting = true;
            shoot.stop();
            linearDrive.start();
        }
        if(!doneShooting) {
            shoot.run();
        } else if(!linearDrive.isFinished()) {
            linearDrive.run();
        }
    }

    @Override
    public void stop() {
        shoot.stop();
        linearDrive.stop();
        doneShooting = false;
    }

    @Override
    public boolean isRunning() {
        return shoot.isFinished() && linearDrive.isFinished();
    }
}
