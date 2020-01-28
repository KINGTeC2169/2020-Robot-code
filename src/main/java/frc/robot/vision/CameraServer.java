package frc.robot.vision;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

public class CameraServer implements Runnable {
    @Override
    public void run() {
        Mat image = new Mat();
        VideoCapture camera = new VideoCapture("http://10.21.69.52:5801");
        camera.read(image);
        System.out.println(image.get(0, 0));
    }
}
