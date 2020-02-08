package frc.robot.subsystems;

import frc.util.ActuatorMap;
import frc.util.Constants;
import frc.util.Controls;
import frc.util.drivers.ControllerFactory;
import frc.util.drivers.DInput;
import frc.util.drivers.Talon;
import frc.util.drivers.Victor;

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

    private boolean noBallsReceived = true;
    private boolean feederRunning = false;
    private boolean enterSensorActivated = false;
    private boolean exitSensorActivated = false;
    private boolean reloading = false;
    private int ballsInFeeder = 0;

    public Indexer() {
        controls = Controls.getInstance();

        feeder = ControllerFactory.masterTalon(ActuatorMap.indexer, false);
        funnel = ControllerFactory.victor(ActuatorMap.funnel, false);

        indexerEnter = new DInput(ActuatorMap.indexerSensorEnter);
        indexerExit = new DInput(ActuatorMap.indexerSensorExit);
        indexerEnter.setName("Indexer Enter");
        indexerExit.setName("Indexer Exit");

        feeder.setName("Feeder");
    }

    public void setFeeder(boolean on) {
        feederRunning = on;
    }

    public int getBallsInFeeder() {
        return ballsInFeeder;
    }

    @Override
    public void update() {
        // Count balls
        boolean enterSensorTripped = false;
        if(indexerEnter.get() && !enterSensorActivated) {
            noBallsReceived = false;
            enterSensorTripped = true;
            ballsInFeeder++;
        }
        if(indexerExit.get() && !exitSensorActivated) {
            ballsInFeeder--;
        }
        enterSensorActivated = indexerEnter.get();
        exitSensorActivated = indexerExit.get();

        // Law 1
        if(ballsInFeeder < 3 && enterSensorTripped) {
            feeder.zeroSensor();
            reloading = true;
        }

        // Law 2
        if(feederRunning) {
            funnel.setOutput(1);
        } else {
            funnel.setOutput(0);
        }

        // Law 3
        if(controls.xbox.getRawButton(6) && !reloading) {
            feeder.zeroSensor();
            reloading = true;
        }

        // Reload
        if(feeder.getSensor() < Constants.feederHalfway && !noBallsReceived) {
            feeder.setOutput(1);
        } else {
            feeder.setOutput(0);
            reloading = false;
        }

        if(feeder.getOutput() > .5) {
            feeder.changeSensor(80);
        }
    }

    @Override
    public void reset() {
        noBallsReceived = true;
        feederRunning = false;
        enterSensorActivated = false;
        exitSensorActivated = false;
        reloading = false;
        ballsInFeeder = 0;

        feeder.zeroSensor();
        feeder.setOutput(0);
        funnel.setOutput(0);
    }
}
