package frc.util;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.states.RobotState;
import frc.robot.subsystems.Indexer;
import frc.robot.subsystems.Superstructure;
import frc.util.drivers.ColorSensor;
import frc.util.drivers.Limelight;
import frc.util.geometry.Vector2;

import java.util.ArrayList;

public class Debug {
    // Serial communication
    private static SerialPort serialPort = null;

    // Vision
    private static final boolean targetInformation = true;
    private static final boolean targetCorners = true;
    private static final boolean visionPositionEstimate = true;

    // Color Sensor
    private static final boolean colorSensor = true;

    // Robot state
    private static final boolean positionEstimate = true;

    public static void start() {
        serialPort = new SerialPort(9600, SerialPort.Port.kMXP);
    }

    public static void init() {
        serialPort.readString();
    }

    public static void debugAll() {
        vision(Limelight.getInstance());
        colorSensor(ColorSensor.getInstance());
        state(RobotState.getInstance());
        readSerial();
        out("Balls in feeder", Superstructure.getInstance().getBallsInFeeder());
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

    public static void readSerial() {
        // Read next packet
        int n = serialPort.read(1)[0];
        byte[] packet = serialPort.read(5*n+1);

        String toPrint = "";
        for(int i = 0; i < packet.length; i++) {
            toPrint += (packet[i] & 0xff)+",";
        }
        out("Packet", toPrint);

        int[] balls = new int[3*n];
        for(int i = 0; i < n; i++) {
            int radius = packet[5*i] & 0xff;
            int x2 = packet[5*i+1] & 0xff;
            int x1 = packet[5*i+2] & 0xff;
            int y2 = packet[5*i+3] & 0xff;
            int y1 = packet[5*i+4] & 0xff;
            out("x2", x2);
            out("x1", x1);
            int x = x2 * 256 + x1;
            int y = y2 * 256 + y1;
            balls[3*i] = radius;
            balls[3*i+1] = x;
            balls[3*i+2] = y;
        }

        StringBuilder readableOutput = new StringBuilder();
        for(int b : balls) {
            readableOutput.append(b).append(",");
        }
        out("Serial Output", readableOutput.toString());
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

    public static void putBoolean(String key, boolean bool) {
        SmartDashboard.putBoolean(key, bool);
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

    public static String getString(String key) {
        if(!SmartDashboard.containsKey(key)) {
            SmartDashboard.putString(key, "");
            return "";
        } else {
            return SmartDashboard.getString(key, "");
        }
    }

    public static void warn(String warning) {
        DriverStation.reportWarning(warning, true);
    }

    private static void out(String key, Object x) {
        SmartDashboard.putString(key, x.toString());
    }
}
