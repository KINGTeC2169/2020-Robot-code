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
            out("Distance", limelight.getDistance());
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

    public static void putNumber(String key, double x) {
        SmartDashboard.putNumber(key, x);
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
