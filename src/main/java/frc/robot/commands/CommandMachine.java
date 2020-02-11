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
    private IndexerCommand idxCommand;

    private CommandMachine() {
        controls = Controls.getInstance();
        dCommand = DriveCommand.getInstance();
        idxCommand = IndexerCommand.getInstance();
    }

    public void teleop() {
        dCommand.teleop();
        idxCommand.teleop();
    }

    public DriveCommand getDriveCommand() {
        return dCommand;
    }

    public IndexerCommand getIndexerCommand() {
        return idxCommand;
    }
}
