package frc.util;

public class PID {
    /* Simple interface for a PID control loop */

    public double P;
    public double I;
    public double D;

    private Double lastError;
    private double[] integral = new double[50];

    public PID(final double P, final double I, final double D) {
        this.P = P;
        this.I = I;
        this.D = D;
    }

    private int i = 0;
    public double getOutput(double error) {
        if(lastError == null) {
            lastError = error;
        }
        integral[i] = error;

        double accumulator = 0;
        for(double e : integral) {
            accumulator += e * .02; // Riemann would be proud
        }

        double output = P * error + D * (lastError - error) / .02 + I * accumulator;
        lastError = error;
        i = (i + 1) % 50;
        return output;
    }
}
