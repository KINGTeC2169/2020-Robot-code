package frc.util;

import edu.wpi.first.wpilibj.SerialPort;
import frc.util.geometry.Vector2;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    private static Map<Integer, Ball> balls = new HashMap<Integer, Ball>();
    private static long time = System.currentTimeMillis();

    public static void spawnThread() {
        serialPort = new SerialPort(9600, SerialPort.Port.kMXP);

        while(true) {
            try {
                Thread.sleep(18);
            } catch (InterruptedException e) {
                e.printStackTrace();
                continue;
            }

            long dt = System.currentTimeMillis() - time;
            time = System.currentTimeMillis();

            // Read next packet
            byte[] next = serialPort.read(1);
            if(next.length < 1) {
                Debug.putString("Serial", "Disconnected");
                continue;
            }
            int n = next[0];
            byte[] packet = serialPort.read(5*n+1);

            ArrayList<Ball> balls = new ArrayList<>();
            for(int i = 0; i < n; i++) {
                int radius = packet[5*i] & 0xff;
                int x2 = packet[5*i+1] & 0xff;
                int x1 = packet[5*i+2] & 0xff;
                int y2 = packet[5*i+3] & 0xff;
                int y1 = packet[5*i+4] & 0xff;
                int x = x2 * 256 + x1;
                int y = y2 * 256 + y1;
                balls.add(new Ball(radius, x, y));
            }

            for(Ball ball : getActiveBalls()) {
                if(balls.indexOf(ball) == -1) {
                    ball.outOfSight(dt);
                }
            }

            String print = "";
            DecimalFormat f = new DecimalFormat("#00.0");
            for(Ball ball : balls) {
                print += ball.idx + " " + f.format(ball.radius) + " " + f.format(ball.position.x) + " " + f.format(ball.position.y) + ";";
            }
            if(n != 0) {
                Debug.putString("Serial", print);
            } else {
                Debug.putString("Serial", "Empty");
            }
        }
    }

    private BallTracker() {

    }

    public void enable() {
        serialPort.readString();
    }

    public static class Ball {
        public final Integer idx;
        public final double radius;
        public final Vector2 position;

        private long outOfSight = 0; // Frames the ball was out of sight
        private boolean active = true;

        public Ball(int radius, int x, int y) {
            this.radius = radius * 61 / 640.;
            position = new Vector2((x - 320) * 61 / 640., (y - 240) * 61 / 640.);

            ArrayList<Ball> activeBalls = getActiveBalls();
            Integer i = -1;
            // Search for a ball that only moves a little
            for(Ball b : activeBalls) {
                if(b.position.sub(position).norm() < Constants.maxPosChange && Math.abs(b.radius - this.radius) < Constants.maxRadiusChange) {
                    i = b.idx; // That's our ball
                    break;
                }
            }

            if(i == -1) {
                // It's a new ball
                idx = getNewIndex();
                balls.put(idx, this);
            } else {
                // It's an old ball
                idx = i;
                balls.replace(idx, this);
            }
        }

        public void outOfSight(long dt) {
            if((outOfSight += dt) >= Constants.maxOutOfSightTime) {
                // Balls been out of sight for too long, deactivate it
                active = false;
            }
        }

        public boolean isActive() {
            return active;
        }
    }

    public Map<Integer, Ball> getBalls() {
        return balls;
    }

    public boolean canSeeBall() {
        return balls != null && getActiveBalls().size() >= 1;
    }

    public Ball getLargestBall() {
        ArrayList<Ball> activeBalls = getActiveBalls();
        if(balls == null || activeBalls.size() < 1) {
            return null;
        }

        Ball largest = null;
        for(Ball ball : activeBalls) {
            if(largest == null || ball.radius > largest.radius) {
                largest = ball;
            }
        }

        return largest;
    }

    public Ball[] getLargestTwo() {
        ArrayList<Ball> activeBalls = getActiveBalls();
        if(balls == null || activeBalls.size() < 2) {
            return null;
        }

        Ball[] largest = {null, null};
        for(Ball ball : activeBalls) {
            if(largest[0] == null || ball.radius > largest[0].radius) {
                largest[0] = ball;
            } else if(largest[1] == null || ball.radius > largest[1].radius) {
                largest[1] = ball;
            }
        }

        return largest;
    }

    private static ArrayList<Ball> getActiveBalls() {
        ArrayList<Ball> activeBalls = new ArrayList<>();
        for(Ball ball : balls.values()) {
            if(ball.isActive()) {
                activeBalls.add(ball);
            }
        }
        return activeBalls;
    }

    private static Integer getNewIndex() {
        int lastKey = -1;
        for(Integer key : balls.keySet()) {
            if(key > lastKey) {
                lastKey = key;
            }
        }
        return lastKey + 1;
    }
}
