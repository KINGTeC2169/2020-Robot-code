package frc.robot.commands;

import frc.util.Controls;

public class CommandMachine {
    private static CommandMachine instance;
    public static CommandMachine getInstance() {
        if(instance == null) {
            return instance = new CommandMachine();
        } else {
            return instance;
        }
    }

    private Controls controls;
    private DriveCommand dCommand;

    private CommandMachine() {
        controls = Controls.getInstance();
        dCommand = DriveCommand.getInstance();
    }

    public void teleop() {
        dCommand.teleop();
    }

    public DriveCommand getDriveCommand() {
        return dCommand;
    }
}
