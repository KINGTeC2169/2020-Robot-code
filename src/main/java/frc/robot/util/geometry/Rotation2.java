package frc.robot.util.geometry;

import java.text.DecimalFormat;

public class Rotation2 {
    private double sin;
    private double cos;

    public Rotation2() {
        sin = 0;
        cos = 0;
    }

    public Rotation2(double angle) {
        this(Math.cos(angle), Math.sin(angle), false);
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
    }

    @Override
    public String toString() {
        DecimalFormat f = new DecimalFormat("#0.000");
        return "Sin: " + f.format(sin) + " Cos:" + f.format(cos);
    }
}
