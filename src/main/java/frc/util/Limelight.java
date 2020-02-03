package frc.util;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
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

    public void start() {
        limelight = NetworkTableInstance.getDefault().getTable("limelight");
    }

    public Vector2 getCenter() {
        double x = limelight.getEntry("tx").getDouble(0);
        double y = limelight.getEntry("ty").getDouble(0);
        return new Vector2(x, y);
    }

    public double targetArea() {
        return limelight.getEntry("ta").getDouble(0);
    }

    public boolean isValidTarget() {
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
        return Constants.cameraToPowerPort / Math.tan(Conversion.degToRad(limelight.getEntry("ty").getDouble(0)));
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
