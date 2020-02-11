package frc.robot.subsystems;

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
    public static Indexer getInstance() {
        if(instance == null) {
            return instance = new Indexer();
        } else {
            return instance;
        }
    }

    private final Controls controls;
    private final Talon feeder;
    private final Victor funnel;
    private final DInput indexerEnter;
    private final DInput indexerExit;

    private boolean slowFlywheel = false;
    private boolean funnelRunning = false;
    private boolean enterSensorActivated = false;
    private boolean exitSensorActivated = false;
    private enum LoadMode {
            halfLoad, fullLoad, shoot
    }
    private LoadMode loadMode = LoadMode.halfLoad;
    private double lastSensor = 0;
    private ArrayList<Double> balls;

    public Indexer() {
        balls = new ArrayList<>();
        balls.add(Constants.feederHalfway * 2);
        balls.add(.0);
        balls.add(.0);

        controls = Controls.getInstance();

        feeder = ControllerFactory.masterTalon(ActuatorMap.indexer, false);
        funnel = ControllerFactory.victor(ActuatorMap.funnel, false);

        indexerEnter = new DInput(ActuatorMap.indexerSensorEnter);
        indexerExit = new DInput(ActuatorMap.indexerSensorExit);
        indexerEnter.setName("Indexer Enter");
        indexerExit.setName("Indexer Exit");

        funnel.setName("Funnel");
        feeder.setName("Feeder");
    }

    public void setFunnel(boolean on) {
        funnelRunning = on;
    }

    public void setSlowFlywheel(boolean slow) {
        slowFlywheel = slow;
    }

    public int getBallsInFeeder() {
        return balls.size();
    }

    public boolean isShooting() {
        return loadMode == LoadMode.shoot;
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
        if(balls.size() == 1 && enterSensorTripped && loadMode != LoadMode.shoot) {
            loadMode = LoadMode.halfLoad;
        }
        if(balls.size() == 2 && enterSensorTripped && loadMode != LoadMode.shoot) {
            loadMode = LoadMode.fullLoad;
        }

        // Law 2
        if(funnelRunning) {
            funnel.setOutput(1);
        } else {
            funnel.setOutput(0);
        }

        // Law 3
        if(balls.size() != 0 && controls.xbox.getRawButton(6) && !slowFlywheel) {
            loadMode = LoadMode.shoot;
        }
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
        if(loadMode == LoadMode.halfLoad) {
            if(balls.size() != 0 && balls.get(0) < Constants.feederHalfway) {
                feeder.setOutput(1);
            } else {
                feeder.setOutput(0);
            }
        }
        if(loadMode == LoadMode.fullLoad) {
            if(balls.size() != 0 && balls.get(0) < 2 * Constants.feederHalfway) {
                feeder.setOutput(1);
            } else {
                feeder.setOutput(0);
            }
        }
        if(loadMode == LoadMode.shoot) {
            feeder.setOutput(1);
        }

        if(feeder.getOutput() > .5) {
            feeder.changeSensor(80);
        }
    }

    public void shoot(boolean shoot) {
        if(!slowFlywheel && shoot) {
            loadMode = LoadMode.shoot;
        } else {
            loadMode = LoadMode.fullLoad;
        }
    }

    @Override
    public void reset() {
        funnelRunning = false;
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
