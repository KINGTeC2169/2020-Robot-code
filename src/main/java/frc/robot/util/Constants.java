package frc.robot.util;

public class Constants {
    /* Physical measurements */

    public static final double driveWheelDiameter = 8;

    /* PID related things */

    public static final double linearDriveTurnP = 0;
    public static final double linearDriveDriveP = 0.00018;
    public static final double linearDriveDriveD = 0.001;

    public static final double turnInPlaceP = 0;
    public static final double turnInPlaceAllowedError = 0;
    public static final double turnInPlaceD = 0;

    /* Other constants */

    public static final boolean encoderPositionPrediction = true;
    public static final boolean visionPositionPrediction = true;

    // Any change in position greater than this many inches won't be counted
    public static final double maxPositionChange = 10;
    // Once we've hit this many similar outliers, they're no longer outliers
    public static final int consecutiveOutliers = 50;
}
