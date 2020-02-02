package frc.robot.util;

public class PD {
    /* Simple interface for a proportional derivative control loop */

    private final double P;
    private final double D;
    private Double lastError;

    public PD(final double P, final double D) {
        this.P = P;
        this.D = D;
    }

    public double getOutput(double error) {
        if(lastError == null) {
            lastError = error;
        }
        return P * error + D * (error - lastError);
    }
}
