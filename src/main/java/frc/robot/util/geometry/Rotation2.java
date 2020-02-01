package frc.robot.util.geometry;

import java.text.DecimalFormat;

public class Rotation2 {
    public final double sin;
    public final double cos;
    public final double angle;

    public Rotation2() {
        sin = 0;
        cos = 1;
        this.angle = 0;
    }

    public Rotation2(double angle) {
        cos = Math.cos(angle);
        sin = Math.sin(angle);
        this.angle = angle;
    }

    public Rotation2(double x, double y) {
        this(x, y, false);
    }

    public Rotation2(double x, double y, boolean normalize) {
        if (normalize) {
            double magnitude = Math.hypot(x, y);
            if (magnitude > 1e-12) {
                sin = y / magnitude;
                cos = x / magnitude;
            } else {
                sin = 0;
                cos = 1;
            }
        } else {
            cos = x;
            sin = y;
        }
        angle = Math.atan2(sin, cos);
    }

    public Rotation2 rotate(Rotation2 rotation) {
        return new Rotation2(rotation.angle + angle);
    }

    // Rotate 90 degrees counterclockwise
    public Rotation2 rotateCC() {
        return new Rotation2(cos, -sin);
    }

    // Rotate 90 degrees clockwise
    public Rotation2 rotateC() {
        return new Rotation2(-cos, sin);
    }

    // Rotate 180 degrees
    public Rotation2 inverse() {
        return new Rotation2(-sin, -cos);
    }

    @Override
    public String toString() {
        DecimalFormat f = new DecimalFormat("#0.000");
        return "Sin: " + f.format(sin) + " Cos:" + f.format(cos);
    }

    public static Rotation2 rightAngle() {
        return new Rotation2(1, 0);
    }
}
