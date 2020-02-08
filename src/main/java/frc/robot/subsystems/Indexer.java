package frc.robot.subsystems;

import frc.util.ActuatorMap;
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

    private final Talon feeder;
    private final Victor funnel;
    private final DInput indexerEnter;
    private final DInput indexerExit;

    private boolean running = false;
    private boolean enterSensorActivated = false;
    private boolean exitSensorActivated = false;
    private int ballsInIndexer = 0;

    public Indexer() {
        feeder = ControllerFactory.masterTalon(ActuatorMap.indexer, false);
        funnel = ControllerFactory.victor(ActuatorMap.funnel, false);

        indexerEnter = new DInput(ActuatorMap.indexerSensorEnter);
        indexerExit = new DInput(ActuatorMap.indexerSensorExit);

        feeder.setName("Feeder");
    }

    public void setIndexer(boolean on) {
        running = on;
    }

    @Override
    public void update() {
        // Count balls
        if(indexerEnter.get() && !enterSensorActivated) ballsInIndexer++;
        if(indexerExit.get() && !exitSensorActivated) ballsInIndexer--;
        enterSensorActivated = indexerEnter.get();
        exitSensorActivated = indexerExit.get();

        /*
        * Law 1: If 0 or 1 balls in the feeder and ball enters feeder, complete reload
        * */

        /*
        * Law 2: If intake is running or was running <3 seconds ago, run the funnel
        * */

        /*
        * Law 3: If shooting, run reload until ball comes out
        * */

        /*
        * Law 4: If >= 3 balls in feeder, don't run feeder until shooting (but complete reload first)
        * */

        if(running) {
            funnel.setOutput(1);
        } else {
            funnel.setOutput(0);
        }
    }

    @Override
    public void reset() {

    }
}
