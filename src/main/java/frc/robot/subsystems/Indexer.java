package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import frc.robot.commands.IndexerCommand;
import frc.util.ActuatorMap;
import frc.util.Constants;
import frc.util.drivers.ControllerFactory;
import frc.util.drivers.DInput;

import java.util.ArrayList;

public class Indexer implements Subsystem {
    private static Indexer instance;
    protected static Indexer getInstance(IndexerCommand idxCommand) {
        if(!Constants.indexerEnabled) {
            return null;
        } else if(instance == null) {
            return instance = new Indexer(idxCommand);
        } else {
            return instance;
        }
    }

    private final IndexerCommand idxCommand;
    private final TalonSRX feeder;
    private final TalonSRX funnel;
    private final DInput indexerEnter;
    private final DInput indexerExit;

    private boolean slowFlywheel = false;
    private boolean enterSensorActivated = true;
    private boolean exitSensorActivated = true;
    private boolean shooting = false;
    private enum LoadMode {
            halfLoad, fullLoad
    }
    private LoadMode loadMode = LoadMode.halfLoad;
    private double lastSensor = 0;
    private ArrayList<Double> balls;

    private Indexer(IndexerCommand idxCommand) {
        this.idxCommand = idxCommand;

        balls = new ArrayList<>();

        feeder = ControllerFactory.masterTalon(ActuatorMap.indexer, true);
        funnel = ControllerFactory.masterTalon(ActuatorMap.funnel, false);

        feeder.setSelectedSensorPosition(0, 0, Constants.talonTimeoutMs);
        feeder.setSensorPhase(false);

        indexerEnter = new DInput(ActuatorMap.indexerSensorEnter);
        indexerExit = new DInput(ActuatorMap.indexerSensorExit);
        indexerEnter.setName("Indexer Enter");
        indexerExit.setName("Indexer Exit");
    }

    public void setSlowFlywheel(boolean slow) {
        slowFlywheel = slow;
    }

    public int getBallsInFeeder() {
        return balls.size();
    }

    public boolean isShooting() {
        if(balls.size() != 0 && idxCommand.isShoot() && !slowFlywheel) {
            return shooting = true;
        } else {
            return shooting;
        }
    }

    @Override
    public void update() {
        // Count balls
        boolean enterSensorReleased = false;
        boolean exitSensorReleased = false;
        if(indexerEnter.get() && !enterSensorActivated) {
            enterSensorReleased = true;
            balls.add(0.0);
        }
        if(balls.size() > 0 && balls.get(0) >= Constants.feederToFlywheelLength) {
            shooting = false;
            balls.remove(0);
            exitSensorReleased = true;
        }
        enterSensorActivated = indexerEnter.get();
        exitSensorActivated = indexerExit.get();

        // Update load mode
        if(enterSensorReleased || exitSensorReleased) {
            if(balls.size() <= 1) {
                loadMode = LoadMode.halfLoad;
            } else {
                loadMode = LoadMode.fullLoad;
            }
        }

        // Update ball locations
        double dSensor = feeder.getSelectedSensorPosition(0) - lastSensor;
        for(int i = 0; i < balls.size(); i++) {
            balls.set(i, balls.get(i) + dSensor);
        }
        lastSensor = feeder.getSelectedSensorPosition(0);

        // Funnel condition
        if(
                ballsPlaced() &&
                (balls.size() < 2 && idxCommand.isRunFunnel() ||
                balls.size() < 3 && isShooting())
        ) {
            funnel.set(ControlMode.PercentOutput, .3);
        } else {
            funnel.set(ControlMode.PercentOutput, 0);
        }

        // Feeder condition
        if(
                !indexerEnter.get() && balls.size() < 2 ||
                !ballsPlaced() ||
                isShooting() ||
                balls.size() == 0 && idxCommand.isShoot()
        ) {
            feeder.set(ControlMode.PercentOutput, .3);
        } else {
            feeder.set(ControlMode.PercentOutput, 0);
        }
    }

    private boolean ballsPlaced() {
        if(balls.size() == 0) {
            return true;
        } else if(loadMode == LoadMode.halfLoad) {
            return balls.get(0) >= Constants.feederHalfLength;
        } else if(loadMode == LoadMode.fullLoad) {
            return balls.get(0) >= Constants.feederLength;
        } else {
            return true;
        }
    }

    @Override
    public void reset() {
        enterSensorActivated = true;
        exitSensorActivated = false;
        loadMode = LoadMode.halfLoad;
        lastSensor = 0;

        balls = new ArrayList<>();
        /*balls.add(Constants.feederHalfway * 2);
        balls.add(.0);
        balls.add(.0);*/

        feeder.setSelectedSensorPosition(0, 0, Constants.talonTimeoutMs);
        feeder.set(ControlMode.PercentOutput, 0);
        funnel.set(ControlMode.PercentOutput, 0);
    }
}
