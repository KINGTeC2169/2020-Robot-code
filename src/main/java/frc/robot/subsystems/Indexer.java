package frc.robot.subsystems;

import frc.robot.commands.IndexerCommand;
import frc.util.ActuatorMap;
import frc.util.Constants;
import frc.util.Controls;
import frc.util.drivers.ControllerFactory;
import frc.util.drivers.DInput;
import frc.util.drivers.Talon;
import frc.util.drivers.Victor;

import java.util.ArrayList;

public class Indexer implements Subsystem {
    private static Indexer instance;
    protected static Indexer getInstance(IndexerCommand idxCommand) {
        if(instance == null) {
            return instance = new Indexer(idxCommand);
        } else {
            return instance;
        }
    }

    private final IndexerCommand idxCommand;
    private final Talon feeder;
    private final Victor funnel;
    private final DInput indexerEnter;
    private final DInput indexerExit;

    private boolean slowFlywheel = false;
    private boolean enterSensorActivated = false;
    private boolean exitSensorActivated = false;
    private enum LoadMode {
            halfLoad, fullLoad
    }
    private LoadMode loadMode = LoadMode.halfLoad;
    private double lastSensor = 0;
    private ArrayList<Double> balls;

    private Indexer(IndexerCommand idxCommand) {
        this.idxCommand = idxCommand;

        balls = new ArrayList<>();
        balls.add(Constants.feederHalfway * 2);
        balls.add(.0);
        balls.add(.0);

        feeder = ControllerFactory.masterTalon(ActuatorMap.indexer, false);
        funnel = ControllerFactory.victor(ActuatorMap.funnel, false);

        indexerEnter = new DInput(ActuatorMap.indexerSensorEnter);
        indexerExit = new DInput(ActuatorMap.indexerSensorExit);
        indexerEnter.setName("Indexer Enter");
        indexerExit.setName("Indexer Exit");

        funnel.setName("Funnel");
        feeder.setName("Feeder");
    }

    public void setSlowFlywheel(boolean slow) {
        slowFlywheel = slow;
    }

    public int getBallsInFeeder() {
        return balls.size();
    }

    public boolean isShooting() {
        return balls.size() != 0 && idxCommand.isShoot() && !slowFlywheel;
    }

    @Override
    public void update() {
        // Count balls
        boolean enterSensorTripped = false;
        boolean exitSensorTripped = false;
        if(indexerEnter.get() && !enterSensorActivated) {
            enterSensorTripped = true;
            balls.add(0.0);
        }
        if(indexerExit.get() && !exitSensorActivated) {
            balls.remove(0);
            exitSensorTripped = true;
        }
        enterSensorActivated = indexerEnter.get();
        exitSensorActivated = indexerExit.get();

        // Law 1
        if(balls.size() == 1 && enterSensorTripped) {
            loadMode = LoadMode.halfLoad;
        }
        if(balls.size() == 2 && enterSensorTripped) {
            loadMode = LoadMode.fullLoad;
        }

        // Law 2
        if(idxCommand.isRunFunnel()) {
            funnel.setOutput(1);
        } else {
            funnel.setOutput(0);
        }

        // Law 3
        if(exitSensorTripped && balls.size() == 1) {
            loadMode = LoadMode.halfLoad;
        } else if(exitSensorTripped) {
            loadMode = LoadMode.fullLoad;
        }

        // Update feeder
        double dSensor = feeder.getSensor() - lastSensor;
        for(int i = 0; i < balls.size(); i++) {
            balls.set(i, balls.get(i) + dSensor);
        }
        lastSensor = feeder.getSensor();

        // Reload
        if(idxCommand.isLoad() && loadMode == LoadMode.halfLoad) {
            if(balls.size() != 0 && balls.get(0) < Constants.feederHalfway) {
                feeder.setOutput(1);
            } else {
                feeder.setOutput(0);
            }
        }
        if(idxCommand.isLoad() && loadMode == LoadMode.fullLoad) {
            if(balls.size() != 0 && balls.get(0) < 2 * Constants.feederHalfway) {
                feeder.setOutput(1);
            } else {
                feeder.setOutput(0);
            }
        }
        if(isShooting()) {
            feeder.setOutput(1);
        }

        // For testing
        if(feeder.getOutput() > .5) {
            feeder.changeSensor(80);
        }
    }

    @Override
    public void reset() {
        enterSensorActivated = false;
        exitSensorActivated = false;
        loadMode = LoadMode.halfLoad;
        lastSensor = 0;

        balls = new ArrayList<>();
        balls.add(Constants.feederHalfway * 2);
        balls.add(.0);
        balls.add(.0);

        feeder.zeroSensor();
        feeder.setOutput(0);
        funnel.setOutput(0);
    }
}
