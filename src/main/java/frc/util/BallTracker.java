package frc.util;

import edu.wpi.first.wpilibj.SerialPort;
import frc.util.geometry.Vector2;

import java.text.DecimalFormat;

public class BallTracker {
    private static BallTracker instance;
    public static BallTracker getInstance() {
        if(instance == null) {
            return instance = new BallTracker();
        } else {
            return instance;
        }
    }

    private static SerialPort serialPort;
    private static Ball[] balls = null;

    public static void spawnThread() {
        serialPort = new SerialPort(9600, SerialPort.Port.kMXP);

        while(true) {
            try {
                Thread.sleep(18);
            } catch (InterruptedException e) {
                e.printStackTrace();
                continue;
            }

            // Read next packet
            byte[] next = serialPort.read(1);
            if(next.length < 1) {
                Debug.putString("Serial", "Disconnected");
                continue;
            }
            int n = next[0];
            byte[] packet = serialPort.read(5*n+1);

            Ball[] balls = new Ball[n];
            for(int i = 0; i < n; i++) {
                int radius = packet[5*i] & 0xff;
                int x2 = packet[5*i+1] & 0xff;
                int x1 = packet[5*i+2] & 0xff;
                int y2 = packet[5*i+3] & 0xff;
                int y1 = packet[5*i+4] & 0xff;
                int x = x2 * 256 + x1;
                int y = y2 * 256 + y1;
                balls[i] = new Ball(radius, x, y);
            }

            String print = "";
            DecimalFormat f = new DecimalFormat("#00.0");
            for(Ball ball : balls) {
                print += f.format(ball.radius) + " " + f.format(ball.position.x) + " " + f.format(ball.position.y) + ";";
            }
            if(n != 0) {
                Debug.putString("Serial", print);
            } else {
                Debug.putString("Serial", "Empty");
            }

            BallTracker.balls = balls;
        }
    }

    private BallTracker() {

    }

    public void enable() {
        serialPort.readString();
    }

    public static class Ball {
        public final double radius;
        public final Vector2 position;

        public Ball(int radius, int x, int y) {
            this.radius = radius * 61 / 640.;
            position = new Vector2((x - 320) * 61 / 640., (y - 240) * 61 / 640.);
        }
    }

    public Ball[] getBalls() {
        return balls;
    }

    public boolean canSeeBall() {
        return balls != null && balls.length >= 1;
    }

    public Ball getLargestBall() {
        if(balls == null || balls.length < 1) {
            return null;
        }

        Ball largest = balls[0];
        for(int i = 1; i < balls.length; i++) {
            if(balls[i].radius > largest.radius) {
                largest = balls[i];
            }
        }

        return largest;
    }

    public Ball[] getLargestTwo() {
        if(balls == null || balls.length < 2) {
            return null;
        }

        Ball[] largest = {balls[0], balls[1]};
        for(int i = 2; i < balls.length; i++) {
            if(balls[i].radius > largest[0].radius) {
                largest[0] = balls[i];
            } else if(balls[i].radius > largest[1].radius) {
                largest[1] = balls[i];
            }
        }

        return largest;
    }
}
