package frc.robot.states;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.util.Debug;

public class VisionState {
    NetworkTable limelight;
    private boolean invalidTarget = true;
    private double tx = 0; // Target X displacement
    private double ty = 0; // Target Y displacement
    private double ta = 0; // Target area

    // Position relative to upper right corner of tape on power port
    private double robotX = 0;
    private double robotY = 0;

    public VisionState() {
        limelight = NetworkTableInstance.getDefault().getTable("limelight");
    }

    public void update() {
        // Basic vision target information
        tx = limelight.getEntry("tx").getDouble(0);
        ty = limelight.getEntry("ty").getDouble(0);
        ta = limelight.getEntry("ta").getDouble(0);
        invalidTarget = limelight.getEntry("tv").getDouble(0) > 1;

        // Rectangle corners
        double[] tcornx = limelight.getEntry("tcornx").getDoubleArray(new double[0]);
        double[] tcorny = limelight.getEntry("tcorny").getDoubleArray(new double[0]);
        int[] idxs = findLargestTwo(tcorny); // Find the two uppermost corners
        // Point 0 should always be to the left of point 1
        if(tcornx[idxs[0]] > tcornx[idxs[1]]) {
            double temp = tcornx[idxs[0]];
            tcornx[idxs[0]] = tcornx[idxs[1]];
            tcornx[idxs[1]] = temp;
        }

        /*
        * Our goal is to calculate the camera's position (x, y) relative to p2
        * p1 and p2 are the upper corners of the vision target
        * d1 and d2 are the points on the ground below P1 and P2
        * c is the camera
        * */

        // Calculate angles from pixel values
        double p1tx = (1/160) * (tcornx[idxs[0]] - 159.5) * Math.tan(54/2);
        double p1ty = (1/120) * (119.5 - tcorny[idxs[0]]) * Math.tan(41/2);
        double p2tx = (1/160) * (tcornx[idxs[1]] - 159.5) * Math.tan(54/2);
        double p2ty = (1/120) * (119.5 - tcorny[idxs[1]]) * Math.tan(41/2);

        // Calculate lengths between the camera and the points on the ground
        double cd1 = 98.25 / Math.tan(p1ty);
        double cd2 = 98.25 / Math.tan(p2ty);

        // Calculate angle d1d2c
        double d1d2 = 20 * Math.sqrt(3);
        double d1d2c = Math.acos((cd1*cd1 + d1d2*d1d2 - cd2*cd2) / (2*cd1*d1d2)); // Law of cosines

        // Calculate x and y
        robotY = cd2 * Math.sin(d1d2c > Math.PI / 2 ? d1d2c : Math.PI - d1d2c);
        robotX = Math.sqrt(cd2*cd2 - robotY * robotY) * ((d1d2c > Math.PI / 2) ? 1 : -1);

        // Debugging stuff
        if(Debug.targetInformation) {
            SmartDashboard.putNumber("tx", tx);
            SmartDashboard.putNumber("ty", tx);
            SmartDashboard.putNumber("ta", ta);
            SmartDashboard.putBoolean("Invalid Target", invalidTarget);
        }
        if(Debug.targetCornerPixels) {
            SmartDashboard.putNumber("tcornx[0]", tcornx[0]);
            SmartDashboard.putNumber("tcornx[1]", tcornx[1]);
            SmartDashboard.putNumber("tcornx[2]", tcornx[2]);
            SmartDashboard.putNumber("tcornx[3]", tcornx[3]);
            SmartDashboard.putNumber("tcorny[0]", tcorny[0]);
            SmartDashboard.putNumber("tcorny[1]", tcorny[1]);
            SmartDashboard.putNumber("tcorny[2]", tcorny[2]);
            SmartDashboard.putNumber("tcorny[3]", tcorny[3]);
        }
        if(Debug.targetCornerAngles) {
            SmartDashboard.putNumber("p1tx", p1tx);
            SmartDashboard.putNumber("p1ty", p1ty);
            SmartDashboard.putNumber("p2tx", p2tx);
            SmartDashboard.putNumber("p2ty", p2ty);
        }
        if(Debug.visionPositionIntermediate) {
            SmartDashboard.putNumber("cd1", cd1);
            SmartDashboard.putNumber("cd2", cd2);
            SmartDashboard.putNumber("d1d2", d1d2);
            SmartDashboard.putNumber("d1d2c", d1d2c);
        }
        if(Debug.visionPositionEstimate) {
            SmartDashboard.putNumber("robotX", robotX);
            SmartDashboard.putNumber("robotY", robotY);
        }
    }

    // Returns indices of largest two values in array
    private int[] findLargestTwo(double a[]) {
        int l1=0, l2=1;
        for(int i = 2; i < a.length; i++) {
            if(a[i] > a[l1]) {
                l1 = i;
            } else if(a[i] > a[l2]) {
                l2 = i;
            }
        }
        int[] r = {l1, l2};
        return r;
    }

    public double getTx() {
        return tx;
    }

    public double getTy() {
        return ty;
    }

    public double getTa() {
        return ta;
    }

    public boolean isInvalidTarget() {
        return invalidTarget;
    }
}
