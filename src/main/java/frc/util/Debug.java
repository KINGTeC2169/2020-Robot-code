package frc.util;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.states.RobotState;
import frc.robot.subsystems.Superstructure;
import frc.util.drivers.ColorSensor;
import frc.util.drivers.Limelight;
import frc.util.geometry.Vector2;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Debug {
    private static FileWriter csvWriter;

    // Vision
    private static final boolean targetInformation = true;
    private static final boolean targetCorners = false;
    private static final boolean visionPositionEstimate = false;

    // Color Sensor
    private static final boolean colorSensor = false;

    // Robot state
    private static final boolean positionEstimate = true;

    private static String header = "Time";
    private static String line;
    private static boolean headerCompleted = false;

    public static void spawnThread() {
        final int millisPerLoop = 100;
        final int nanosPerLoop = 0;

        String datetime = new SimpleDateFormat("MM-dd-yyyy HH-mm-ss").format(new Date());
        try {
            csvWriter = new FileWriter(datetime + ".csv");
        } catch(IOException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(millisPerLoop, nanosPerLoop);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        debugAll();
    }

    public static void debugAll() {
        line = new SimpleDateFormat("HH:mm:ss").format(new Date());

        flywheel(Superstructure.getInstance());
        vision(Limelight.getInstance());
        colorSensor(ColorSensor.getInstance());
        out("Balls in feeder", Superstructure.getInstance().getBallsInFeeder());

        try {
            writeCsv();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void flywheel(Superstructure superstructure) {
        out("Flywheel Up to Speed", superstructure.isSlowFlywheel());
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
        }
    }

    public static void colorSensor(ColorSensor colorSensor) {
        if(Debug.colorSensor) {
            SmartDashboard.putString("Color Sensor", colorSensor.toString());
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

    private static void writeCsv() throws IOException {
        if(!headerCompleted) {
            //csvWriter.append(header).append("\n");
            headerCompleted = true;
        }
        //csvWriter.append(line).append("\n");
    }

    private static void out(String key, Object x) {
        line += ",";
        if(!headerCompleted) {
            header += ("," + key);
        }
        line += x.toString();
    }
}
