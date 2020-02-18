package frc.util;

import edu.wpi.first.wpilibj.SerialPort;
import frc.util.geometry.Vector2;

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
            if(next.length < 1) continue;
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
            for(Ball ball : balls) {
                print += ball.radius + " " + ball.position.x + " " + ball.position.y + ";";
            }
            Debug.putString("Serial", print);

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
            this.radius = radius;
            position = new Vector2(x - 240, y - 180);
        }
    }

    public Ball[] getBalls() {
        return balls;
    }

    public Ball getLargestBall() {
        Ball largest = new Ball(0, 240, 240);
        for(Ball ball : balls) {
            if(ball.radius > largest.radius) {
                largest = ball;
            }
        }

        return largest;
    }
}
