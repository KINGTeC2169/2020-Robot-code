package frc.util;

public class Constants {
    /* Physical measurements */

    public static final double cameraToPowerPort = 66.25;
    public static final double ticksPerRotation = 8192;
    public static final double driveWheelDiameter = 8;

    /* Autonomous */

    // Finding target
    public static final double turnTowardsTargetOutput = .25;
    public static final double pointingTowardsTarget = 30;
    public static final double backAwayFromTargetOutput = .5;

    // Getting in range (general)
    public static final double tooCloseOutput = .5;
    public static final double tooFarOutput = .5;
    public static final double tooWideBackup = .5;
    public static final double tooWideSteerAngle = 90;

    // Getting in range for 2 pointers
    public static final double shootingMinY2 = 36;
    public static final double shootingMaxY2 = 240;
    public static final double shootingMaxSlope2 = 1.2;

    // Getting in range for 3 pointers
    public static final double shootingMinY3 = 48;
    public static final double shootingMaxY3 = 216;
    public static final double shootingMaxSlope3 = 0.8;

    /* PID */

    // Aiming
    public static final double visionDriveP = .1;
    public static final double visionDriveD = 0;

    // Getting in range of target
    public static final double turnTowardsTargetP = 0.02;
    public static final double turnTowardsTargetD = 0.2;

    // Aligning to angle on gyro
    public static final double alignToGyroP = 0.005;
    public static final double alignToGyroD = 0.05;

    // Linear Drive
    public static final double linearDriveTurnP = 0;
    public static final double linearDriveDriveP = 0.00018;
    public static final double linearDriveDriveD = 0.001;

    // Turn in place
    public static final double turnInPlaceP = 0;
    public static final double turnInPlaceAllowedError = 0;
    public static final double turnInPlaceD = 0;

    /* Other constants */

    public static final boolean usingTestBed = true;

    // Predicting position
    public static final boolean encoderPositionPrediction = false;
    public static final boolean visionPositionPrediction = true;
    // Any change in position greater than this many inches won't be counted
    public static final double maxPositionChange = 10;
    // Once we've hit this many similar outliers, they're no longer outliers
    public static final int consecutiveOutliers = 50;
}
