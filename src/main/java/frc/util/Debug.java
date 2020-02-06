package frc.util;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.states.RobotState;
import frc.util.drivers.ColorSensor;
import frc.util.drivers.Limelight;
import frc.util.geometry.Vector2;

public class Debug {
    // Vision
    private static final boolean targetInformation = true;
    private static final boolean targetCorners = true;
    private static final boolean visionPositionEstimate = true;

    // Color Sensor
    private static final boolean colorSensor = true;

    // Robot state
    private static final boolean positionEstimate = true;

    public static void debugAll() {
        vision(Limelight.getInstance());
        colorSensor(ColorSensor.getInstance());
        state(RobotState.getInstance());
    }

    public static void vision(Limelight limelight) {
        if(targetInformation) {
            out("Center", limelight.getCenter());
            out("Valid Target?", limelight.isValidTarget());
        }
        if(targetCorners) {
            Vector2[] corners = limelight.getCorners();
            for(int i = 0; i < corners.length; i++) {
                out("Corner " + i, corners[i]);
            }
        }
        if(visionPositionEstimate) {
            putNumber("Distance", limelight.getDistance());
            out("Position", limelight.getPosition());
            out("Rotation", limelight.getRotation());
        }
    }

    public static void colorSensor(ColorSensor colorSensor) {
        if(Debug.colorSensor) {
            SmartDashboard.putString("Color Sensor", colorSensor.toString());
        }
    }

    public static void state(RobotState state) {
        if(positionEstimate) {
            out("Position Estimate", state.getDriveState().getPos());
        }
    }

    public static void visionEstimate(double p1ty, double p2ty, double cd1, double cd2, double d1d2c, Vector2 estimate) {
        if(visionPositionEstimate) {
            putNumber("p1ty", p1ty);
            putNumber("p2ty", p2ty);
            putNumber("cd1", cd1);
            putNumber("cd2", cd2);
            putNumber("d1d2c", d1d2c);
            out("Vision estimate", estimate);
        }
    }

    public static void putNumber(String key, double x) {
        SmartDashboard.putNumber(key, Math.floor(x*10000)/10000);
    }

    public static void putString(String key, String str) {
        SmartDashboard.putString(key, str);
    }

    public static double getNumber(String key) {
        if(!SmartDashboard.containsKey(key)) {
            SmartDashboard.putNumber(key, 0);
            return 0;
        } else {
            return SmartDashboard.getNumber(key, 0);
        }
    }

    public static boolean getBoolean(String key) {
        if(!SmartDashboard.containsKey(key)) {
            SmartDashboard.putBoolean(key, false);
            return false;
        } else {
            return SmartDashboard.getBoolean(key, false);
        }
    }

    private static void out(String key, Object x) {
        SmartDashboard.putString(key, x.toString());
    }
}
