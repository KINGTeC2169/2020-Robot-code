package frc.util;

public class PID {
    /* Simple interface for a PID control loop */

    private final double P;
    private final double I;
    private final double D;

    private Double lastError;
    private double integral = 0;

    public PID(final double P, final double I, final double D) {
        this.P = P;
        this.I = I;
        this.D = D;
    }

    public double getOutput(double error) {
        if(lastError == null) {
            lastError = error;
        }
        integral += I * error * .02; // Riemann would be proud
        double output = P * error + D * (lastError - error) / .02 + I * integral;
        lastError = error;
        return output;
    }
}
