package frc.robot.util.geometry;

import java.text.DecimalFormat;

public class Pos2 {
    public Vector2 translation;
    public Rotation2 rotation;

    public Pos2() {
        translation = new Vector2();
        rotation = new Rotation2();
    }

    public Pos2(Vector2 translation, Rotation2 rotation) {
        this.translation = translation;
        this.rotation = rotation;
    }

    public double getX() {
        return translation.x;
    }

    public double getY() {
        return translation.y;
    }

    public double getAngle() {
        return rotation.angle;
    }

    @Override
    public String toString() {
        DecimalFormat f = new DecimalFormat("#0.000");
        return "(" + f.format(translation.x) + "," + f.format(translation.y) + "," + f.format(Math.toDegrees(rotation.angle)) + " deg)";
    }
}
