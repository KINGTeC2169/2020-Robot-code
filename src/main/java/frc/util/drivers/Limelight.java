package frc.util.drivers;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import frc.util.Constants;
import frc.util.Conversion;
import frc.util.Debug;
import frc.util.geometry.Rotation2;
import frc.util.geometry.Vector2;

public class Limelight {
    private static Limelight instance;
    public static Limelight getInstance() {
        if(instance == null) {
            return instance = new Limelight();
        } else {
            return instance;
        }
    }

    private NetworkTable limelight;
    private final boolean testing;

    public Limelight() {
        testing = !Constants.usingLimelight;
    }

    public void start() {
        limelight = NetworkTableInstance.getDefault().getTable("limelight");
    }

    public Vector2 getCenter() {
        // Distorted values
        double xd = limelight.getEntry("tx").getDouble(0);
        double yd = limelight.getEntry("ty").getDouble(0);
        double r2 = xd * xd + yd * yd; // r squared

        double k = Constants.limelightDistortion;
        double x = xd / (1 + k * r2);
        double y = yd / (1 + k * r2);

        double rx = x * Math.cos(Constants.limelightRotation) - y * Math.sin(Constants.limelightRotation);
        double ry = x * Math.sin(Constants.limelightRotation) + y * Math.cos(Constants.limelightRotation);

        if(testing) {
            x = Debug.getNumber("tx");
            y= Debug.getNumber("ty");
        }
        return new Vector2(rx, ry + Constants.limelightYOffset);
    }

    public static Vector2 undistort(Vector2 v) {
        double r2 = v.x * v.x + v.y * v.y; // r squared
        double k = Constants.limelightDistortion;
        return new Vector2(v.x / (1 + k * r2), v.y / (1 + k * r2));
    }

    public double targetArea() {
        if(testing) {
            return Debug.getNumber("ta");
        }
        return limelight.getEntry("ta").getDouble(0);
    }

    public boolean isValidTarget() {
        if(testing) {
            return Debug.getBoolean("Valid Target");
        }
        return limelight.getEntry("tv").getDouble(0) > 0;
    }

    public Vector2[] getCorners() {
        double[] tcornxy = limelight.getEntry("tcornxy").getDoubleArray(new double[0]);

        if(tcornxy.length == 8) {
            Vector2[] v = {
                    new Vector2(tcornxy[0], tcornxy[1]),
                    new Vector2(tcornxy[2], tcornxy[3]),
                    new Vector2(tcornxy[4], tcornxy[5]),
                    new Vector2(tcornxy[6], tcornxy[7])
            };
            return v;
        } else {
            return new Vector2[0];
        }
    }

    public double getDistance() {
        Debug.putNumber("dist", Constants.cameraToPowerPort / Math.tan(Conversion.degToRad(getCenter().y)));
        return Constants.cameraToPowerPort / Math.tan(Conversion.degToRad(getCenter().y));
    }

    public Vector2 getPosition() {
        double[] camtran = limelight.getEntry("camtran").getDoubleArray(new double[0]);
        if(camtran.length == 6) {
            return new Vector2(camtran[0], camtran[2]);
        } else {
            return new Vector2(0, 0);
        }
    }

    public Rotation2 getRotation() {
        double[] camtran = limelight.getEntry("camtran").getDoubleArray(new double[0]);
        if(camtran.length == 6) {
            return new Rotation2(camtran[4]);
        } else {
            return new Rotation2(0);
        }
    }
}
