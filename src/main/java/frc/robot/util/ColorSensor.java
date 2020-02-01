package frc.robot.util;

import com.revrobotics.ColorSensorV3;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.util.Color;

import static frc.robot.util.ColorSensor.CpColor.*;

public class ColorSensor {

    private static ColorSensor instance;
    public static ColorSensor getInstance() {
        if(instance == null) {
            return instance = new ColorSensor();
        } else {
            return instance;
        }
    }

    private ColorSensorV3 colorSensor = new ColorSensorV3(I2C.Port.kOnboard);
    private Color detected;

    public enum CpColor {
        GREEN, RED, YELLOW, CYAN
    }

    public ColorSensor() {

    }

    // Runs at 50hz
    public void update() {
        detected = colorSensor.getColor();
    }

    public CpColor getColor() {
        double r = detected.red,
                g = detected.green,
                b = detected.blue;

        // Get distance between detected color and each color on the control panel
        double dg = distance(r, g, b, .20, .58, .22);
        double dr = distance(r, g, b, .60, .31, .07);
        double dy = distance(r, g, b, .31, .55, 0.13);
        double dc = distance(r, g, b, .16, .46, .38);

        // Return smallest of four distances
        if(dg < dr && dg < dy && dg < dc) {
            return GREEN;
        } else if(dr < dy && dr < dc) {
            return RED;
        } else if(dy < dc) {
            return YELLOW;
        } else {
            return CYAN;
        }
    }

    // Finds the Euclidean distance between two colors
    private double distance(double r1, double g1, double b1, double r2, double g2, double b2) {
        return Math.sqrt(Math.pow(r1 - r2, 2) + Math.pow(g1 - g2, 2) + Math.pow(b1 - b2, 2));
    }

    public double getRed() {
        return detected.red;
    }

    public double getGreen() {
        return detected.green;
    }

    public double getBlue() {
        return detected.blue;
    }

    public int getProximity() {
        return colorSensor.getProximity();
    }

    public int getIR() {
        return colorSensor.getIR();
    }

    public String toString() {
        switch(getColor()) {
            case GREEN:
                return "Sensor detects GREEN";
            case RED:
                return "Sensor detects RED";
            case YELLOW:
                return "Sensor detects YELLOW";
            case CYAN:
                return "Sensor detects CYAN";
            default:
                return "Sensor is not detecting a color";
        }
    }
}
