package frc.robot.subsystems;

import frc.util.drivers.ColorSensor;

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

    public Patrick() {
        colorSensor = ColorSensor.getInstance();
    }

    @Override
    public void update() {
        ColorSensor.CpColor detectedColor = colorSensor.getColor();
    }

    @Override
    public void reset() {
    }
}
