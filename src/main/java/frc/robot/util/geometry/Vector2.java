package frc.robot.util.geometry;

public class Vector2 {
    public double x;
    public double y;

    public Vector2() {
        x = 0;
        y = 0;
    }

    public Vector2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // If magnAngle is true, create a vector using magnitude and angle
    public Vector2(double magnitude, double angle, boolean magnAngle) {
        if(!magnAngle) {
            x = magnitude * Math.cos(angle);
            y = magnitude * Math.sin(angle);
        } else {
            this.x = magnitude;
            this.y = angle;
        }
    }

    public Vector2 add(Vector2 v) {
        return new Vector2(x + v.x, y + v.y);
    }

    public Vector2 sub(Vector2 v) {
        return new Vector2(x - v.x, y - v.y);
    }

    public Vector2 scale(double scalar) {
        return new Vector2(x * scalar, y * scalar);
    }

    public Vector2 rotate(double angle) {
        return new Vector2(Math.cos(angle) * x - Math.sin(angle) * y, Math.sin(angle) * x + Math.cos(angle) * y);
    }

    public double norm() {
        return Math.sqrt(x*x+y*y);
    }

    public double angle() {return Math.atan2(y, x);}

    // Find the vector for a chord on a circle. For clockwise chords, radius should be negative
    public static Vector2 chord(double radius, double angle1, double angle2) {
        return new Vector2(radius * (Math.cos(angle2) - Math.cos(angle1)), radius * (Math.sin(angle2) - Math.sin(angle1)));
    }

    @Override
    public String toString() {
        return "<" + x + "," + y + ">";
    }
}
