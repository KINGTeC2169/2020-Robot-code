package frc.robot.auto.modes;

import frc.robot.auto.actions.LinearDrive;
import frc.robot.auto.actions.ShootBalls;
import frc.robot.commands.CommandMachine;
import frc.robot.subsystems.Superstructure;

public class Owen implements Mode {
    /*
    * Shoot
    * Drive past line
    * */

    private final Superstructure superstructure;
    private final ShootBalls shoot;
    private final LinearDrive linearDrive;

    private boolean doneShooting = false;

    public Owen(Superstructure superstructure, CommandMachine commandMachine) {
        this.superstructure = superstructure;
        shoot = new ShootBalls(superstructure, commandMachine.getDriveCommand(), commandMachine.getIndexerCommand());
        linearDrive = new LinearDrive(commandMachine.getDriveCommand(), 48);
    }

    @Override
    public void start() {
        shoot.start();
        doneShooting = false;
    }

    @Override
    public void run() {
        if(!doneShooting) {
            shoot.run();
            if(shoot.isFinished()) {
                doneShooting = true;
                shoot.stop();
                linearDrive.start();
            }
        } else if(!linearDrive.isFinished()) {
            linearDrive.update(superstructure.getLinearDriveDistance());
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
