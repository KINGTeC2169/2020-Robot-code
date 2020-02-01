package frc.robot.subsystems;

import frc.robot.util.ColorSensor;

public class ColorSensorAction {
    ColorSensor colorSensor;

    public ColorSensorAction() {
        colorSensor = ColorSensor.getInstance();
    }

    public void update() {
        ColorSensor.CpColor detectedColor = colorSensor.getColor();
    }
}
