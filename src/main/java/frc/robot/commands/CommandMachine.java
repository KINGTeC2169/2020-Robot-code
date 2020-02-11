package frc.robot.commands;

public class CommandMachine {
    private static CommandMachine instance;
    public static CommandMachine getInstance() {
        if(instance == null) {
            return instance = new CommandMachine();
        } else {
            return instance;
        }
    }

    private final DriveCommand dCommand;
    private final IndexerCommand idxCommand;
    private final ShooterCommand sCommand;

    private CommandMachine() {
        dCommand = DriveCommand.getInstance();
        idxCommand = IndexerCommand.getInstance();
        sCommand = ShooterCommand.getInstance();
    }

    public void teleop() {
        dCommand.teleop();
        idxCommand.teleop();
        sCommand.teleop();
    }

    public DriveCommand getDriveCommand() {
        return dCommand;
    }

    public IndexerCommand getIndexerCommand() {
        return idxCommand;
    }

    public ShooterCommand getShooterCommand() {
        return sCommand;
    }
}
