package frc.util;

import java.awt.*;

public class Conversion {
    public static double radToDeg(double rad) {
        return rad * 180 / Math.PI;
    }

    public static double degToRad(double deg) {
        return deg * Math.PI / 180;
    }

    public static double encoderTicksToRotations(double ticks) {
        return ticks / Constants.ticksPerRotation;
    }

    public static double encoderTicksToDegrees(double ticks) {
        return 360 * ticks / Constants.ticksPerRotation;
    }

    public static double rotationsToInches(double rotations, double diameter) {
        return rotations * diameter * Math.PI;
    }

    public static int[] hsvToRgb(double h, double s, double v) {
        int x = Color.HSBtoRGB((float) h, (float) s, (float) v);
        int[] rgb = {(x>>16)&0xFF, (x>>8)&0xFF, x&0xFF};
        return rgb;
    }

    public static double getHoodAngle(boolean isValidTarget, double distance) {
        return isValidTarget ? 90 - 180 * Math.atan(distance / 100) / Math.PI : 45;
    }
}
