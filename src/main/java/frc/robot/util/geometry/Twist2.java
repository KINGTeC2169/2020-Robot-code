package frc.robot.util.geometry;

import java.text.DecimalFormat;

public class Twist2 {
    public double dx;
    public double dy;
    public double dtheta;

    public Twist2(double dx, double dy, double dtheta) {
        this.dx = dx;
        this.dy = dy;
        this.dtheta = dtheta;
    }

    public Pos2 toPos2() {
        double sin_theta = Math.sin(dtheta);
        double cos_theta = Math.cos(dtheta);
        double s, c;
        if (Math.abs(dtheta) < 1E-9) {
            s = 1.0 - 1.0 / 6.0 * dtheta * dtheta;
            c = .5 * dtheta;
        } else {
            s = sin_theta / dtheta;
            c = (1.0 - cos_theta) / dtheta;
        }
        return new Pos2(new Vector2(dx * s - dy * c, dx * c + dy * s),
                new Rotation2(cos_theta, sin_theta, false));
    }

    @Override
    public String toString() {
        DecimalFormat f = new DecimalFormat("#0.000");
        return "(" + f.format(dx) + "," + f.format(dy) + "," + f.format(Math.toDegrees(dtheta)) + " deg)";
    }
}
