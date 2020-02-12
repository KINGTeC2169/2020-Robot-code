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
    private final IntakeCommand iCommand;
    private final PatrickCommand pCommand;
    private final ShooterCommand sCommand;
    private final TelescopeCommand tCommand;

    private CommandMachine() {
        dCommand = DriveCommand.getInstance();
        idxCommand = IndexerCommand.getInstance();
        iCommand = IntakeCommand.getInstance();
        pCommand = PatrickCommand.getInstance();
        sCommand = ShooterCommand.getInstance();
        tCommand = TelescopeCommand.getInstance();
    }

    public void teleop() {
        dCommand.teleop();
        idxCommand.teleop();
        iCommand.teleop();
        pCommand.teleop();
        sCommand.teleop();
        tCommand.teleop();
    }

    public DriveCommand getDriveCommand() {
        return dCommand;
    }

    public IndexerCommand getIndexerCommand() {
        return idxCommand;
    }

    public PatrickCommand getPatrickCommand() {
        return pCommand;
    }

    public ShooterCommand getShooterCommand() {
        return sCommand;
    }

    public IntakeCommand getIntakeCommand() {
        return iCommand;
    }

    public TelescopeCommand getTelescopeCommand() {
        return tCommand;
    }
}
