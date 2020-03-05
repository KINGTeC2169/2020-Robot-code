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

    public static double velocityToRpm(double ticksPerDecisecond) {
        return 600 * ticksPerDecisecond / Constants.ticksPerRotation;
    }

    public static double rpmToVelocity(double rpm) {
        return Constants.ticksPerRotation * rpm / 600;
    }

    public static int[] hsvToRgb(double h, double s, double v) {
        int x = Color.HSBtoRGB((float) h, (float) s, (float) v);
        int[] rgb = {(x>>16)&0xFF, (x>>8)&0xFF, x&0xFF};
        return rgb;
    }

    public static double getDesiredHoodAngle(boolean isValidTarget, double ty) {
        if(!isValidTarget) {
            return 79.4;
        } else if(ty < Constants.farShotPitch) {
            return 30.5;
        } else {
            return .024065 * ty * ty - .907483 * ty + 47.411007;
        }
    }

    public static double getDesiredRpm(boolean isValidTarget, double ty) {
        if(!isValidTarget) {
            return Constants.wallDesiredRpm;
        } else if(ty < Constants.farShotPitch) {
            return Constants.farDesiredRpm;
        } else {
            return Constants.closeDesiredRpm;
        }
    }
}
