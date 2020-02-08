package frc.robot.subsystems;

import frc.util.ActuatorMap;
import frc.util.Controls;
import frc.util.drivers.ColorSensor;
import frc.util.drivers.ControllerFactory;
import frc.util.drivers.Talon;

import java.lang.reflect.Array;

public class Patrick implements Subsystem {
    private static Patrick instance;
    public static Patrick getInstance() {
        if(instance == null) {
            return instance = new Patrick();
        } else {
            return instance;
        }
    }

    ColorSensor colorSensor;
    private Talon wheelSpin = ControllerFactory.masterTalon(ActuatorMap.patrick, false);
    private boolean wheelState = false;
    private boolean colorFound = false;
    private Controls controls;
    private int slices;
    private ColorSensor.CpColor lastCol;
    private final ColorSensor.CpColor colorVal [] = {ColorSensor.CpColor.CYAN, ColorSensor.CpColor.YELLOW, ColorSensor.CpColor.GREEN, ColorSensor.CpColor.RED};

    public Patrick ()
    {
        colorSensor = ColorSensor.getInstance();
        controls = Controls.getInstance();
    }

    public boolean getWheelSpin() {
       if (wheelSpin.getOutput() > .5) {
           wheelState = true;
       }
        return wheelState;
    }

    public ColorSensor getColorSensor() {
        if ()
        {
            colorFound = true;
        }
        return colorSensor;
    }

    public void rotationalControl(ColorSensor.CpColor detectedColor) {
        for (int i = 0; i < colorVal.length; i++) {

            if (detectedColor == colorVal[i]) {
                break;
            }
        }

        if (slices  >= 25) {
            wheelSpin.setOutput(1);
        }
        else {
            wheelSpin.setOutput(0);
        }
    }

    public void positionalControl() {


    }




    public Talon setWheel() {
        if(colorFound && wheelState) {
            wheelState = false;
            Talon wheelSpin
        }
    }

    @Override
    public void update() {
        ColorSensor.CpColor detectedColor = colorSensor.getColor();
        if(controls.xbox.getXButtonPressed())
        {
            rotationalControl();
        }

        detectedColor = lastCol;
    }

    @Override
    public void reset() {
    }
}
