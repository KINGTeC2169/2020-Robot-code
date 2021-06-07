package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
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
    private boolean shotABall = false;
    private int feederCooldownTicks = 0;

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
        if(balls.size() != 0 && idxCommand.isShoot() && !slowFlywheel){
            return shooting = true;
        } else {
            return shooting;
        }
    }

    @Override
    public void update() {

        updateShuffle();
        feederCooldownTicks++;

        // Count balls
        boolean enterSensorReleased = false;
        boolean exitSensorReleased = false;
        if(indexerEnter.get() && !enterSensorActivated) {
            if(feederCooldownTicks > 25){
                enterSensorReleased = true;
                balls.add(0.0);
                feederCooldownTicks = 0;
            }
        }
        if(balls.size() > 0 && balls.get(0) >= Constants.feederToFlywheelLength) {
            shooting = false;
            balls.remove(0);
            exitSensorReleased = true;
            shotABall = true;
        }
        enterSensorActivated = indexerEnter.get();
        exitSensorActivated = indexerExit.get();


        SmartDashboard.putBoolean("Exit Sensor State", exitSensorActivated);
        SmartDashboard.putBoolean("Enter Sensor State", enterSensorActivated);

        // Update load mode
        if(enterSensorReleased || exitSensorReleased) {
            if(balls.size() <= 1) {
//                loadMode = LoadMode.halfLoad;
            } else {
//                loadMode = LoadMode.fullLoad;
            }
        }

        // Update ball locations
        double dSensor = feeder.getSelectedSensorPosition(0) - lastSensor;
        for(int i = 0; i < balls.size(); i++) {
            balls.set(i, balls.get(i) + dSensor);
        }

        lastSensor = feeder.getSelectedSensorPosition(0);

        SmartDashboard.putNumber("Balls.size()", balls.size());
        SmartDashboard.putBoolean("ballsPlaced()", ballsPlaced());
        SmartDashboard.putBoolean("idxCommand.isShoot()", idxCommand.isShoot());
        // Funnel condition
        if(idxCommand.isExhaust()) {
            funnel.set(ControlMode.PercentOutput, -.3);
            balls = new ArrayList<>();
        } else if(
                ballsPlaced() &&
                (balls.size() < 2 && idxCommand.isRunFunnel() ||
                balls.size() < 3 && isShooting() ||
                balls.size() == 0 && idxCommand.isShoot())
        ) {
            funnel.set(ControlMode.PercentOutput, .3);
            SmartDashboard.putString("Funnel Status", "Line 119");
        } else {
            funnel.set(ControlMode.PercentOutput, 0);
            SmartDashboard.putString("Funnel Status", "Line 122");
        }

        SmartDashboard.putBoolean("isShooting", isShooting());

        // Feeder condition
        if(idxCommand.isExhaust()) {
            feeder.set(ControlMode.PercentOutput, -.3);
        } else if(
                //Enter sensor is blocked and there is less than 2 balls in feeder
                !indexerEnter.get() && balls.size() < 2 ||
                // or !ballsPlaced()
                !ballsPlaced() ||
                // or we are shooting
                isShooting()
        ) {
            SmartDashboard.putString("Feeder Status", "Line 132");
            feeder.set(ControlMode.PercentOutput, .3);
        } else {
            SmartDashboard.putString("Feeder Status", "Line 135");
            feeder.set(ControlMode.PercentOutput, 0);
        }
    }

    private boolean ballsPlaced() {
        if(balls.size() == 0){
            return true;
        }
        else if(balls.size() == 1){
            return balls.get(0) >= Constants.feederHalfLength;
        }
        else if(balls.size() == 2){
            return balls.get(0) >= Constants.feederLength;
        }
        return true;
    }

    private void updateShuffle(){
        if(balls.size() > 0){
            SmartDashboard.putNumber("Ball 0", balls.get(0));
        }
        else{
            SmartDashboard.putNumber("Ball 0", -1);
        }
        if(balls.size() > 1){
            SmartDashboard.putNumber("Ball 1", balls.get(1));
        }
        else{
            SmartDashboard.putNumber("Ball 1", -1);
        }
        if(balls.size() > 2){
            SmartDashboard.putNumber("Ball 2", balls.get(2));
        }
        else{
            SmartDashboard.putNumber("Ball 2", -1);
        }
        if(balls.size() > 3){
            SmartDashboard.putNumber("Ball 3", balls.get(3));
        }
        else{
            SmartDashboard.putNumber("Ball 3", -1);
        }
    }

    public boolean isShotABall() {
        return shotABall;
    }

    @Override
    public void reset() {
        shotABall = false;
        enterSensorActivated = true;
        exitSensorActivated = false;
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
