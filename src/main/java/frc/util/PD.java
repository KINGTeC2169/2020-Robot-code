package frc.util;

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
        double output = P * error + D * (lastError - error);
        lastError = error;
        return output;
    }
}
