package frc.robot.util;

public class Conversion {
    public static double radToDeg(double rad) {
        return rad * 180 / Math.PI;
    }

    public static double degToRad(double deg) {
        return deg * Math.PI / 180;
    }

    public static double rotationsToInches(double rotations, double diameter) {
        return rotations * diameter * Math.PI;
    }
}
