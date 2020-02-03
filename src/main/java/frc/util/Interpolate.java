package frc.util;

import frc.util.geometry.Vector2;

public class Interpolate {
    public static double interpolate(double x, double y, double q) {
        return x + (y-x) * q;
    }

    public static Vector2 interpolate(Vector2 u, Vector2 v, double c) {
        return new Vector2(interpolate(u.x, v.x, c), interpolate(u.y, v.y, c));
    }
}
