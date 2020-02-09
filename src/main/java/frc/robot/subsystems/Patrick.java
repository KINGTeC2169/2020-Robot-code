package frc.robot.subsystems;

import edu.wpi.first.wpilibj.DriverStation;
import frc.util.ActuatorMap;
import frc.util.Controls;
import frc.util.Debug;
import frc.util.drivers.ColorSensor;
import frc.util.drivers.ControllerFactory;
import frc.util.drivers.DStation;
import frc.util.drivers.Talon;

public class Patrick implements Subsystem {
    private static Patrick instance;
    public static Patrick getInstance() {
        if(instance == null) {
            return instance = new Patrick();
        } else {
            return instance;
        }
    }

    ColorSensor colorSensor;
    private Talon talon;
    private Controls controls;
    private int slices = 0;
    private ColorSensor.CpColor prevColor;
    private ColorSensor.CpColor desiredColor;
    private final ColorSensor.CpColor colorVal [] = {ColorSensor.CpColor.CYAN, ColorSensor.CpColor.YELLOW, ColorSensor.CpColor.RED, ColorSensor.CpColor.GREEN};

    public Patrick ()
    {
        colorSensor = ColorSensor.getInstance();
        controls = Controls.getInstance();
        talon = ControllerFactory.masterTalon(ActuatorMap.patrick, false);
        talon.setName("Patrick");
    }

    public void rotationalControl(ColorSensor.CpColor detectedColor) {
        // Find detected and previous colors in color order
        int detectedIndex = Integer.MIN_VALUE; // Integer.MIN_VALUE is just a garbage value
        int prevIndex = Integer.MIN_VALUE;
        for (int i = 0; i < colorVal.length; i++) {
            if (detectedColor.equals(colorVal[i])) {
                detectedIndex = i;
            }
            if(prevColor.equals(colorVal[i])) {
                prevIndex = i;
            }
        }

        // Determine direction color panel is moving
        if(detectedIndex - prevIndex == 1 || detectedIndex - prevIndex == -3) {
            slices++;
        } else if(detectedIndex - prevIndex == -1 || detectedIndex - prevIndex == 3) {
            slices--;
        } else if(detectedIndex != prevIndex) {
            Debug.warn("Color sensor skipped a color");
        }
        Debug.putNumber("Control Panel Slices", slices);

        // Spin the wheel
        if (slices < 25) {
            talon.setOutput(1);
        } else {
            talon.setOutput(0);
        }
    }

    public void positionalControl(ColorSensor.CpColor detectedColor) {
        if(desiredColor == null) {
            String gameData = DStation.getInstance().getMessage();
            switch(gameData.length() > 0 ? gameData.charAt(0) : ' ') {
                case 'B':
                    desiredColor = ColorSensor.CpColor.CYAN;
                    break;
                case 'G':
                    desiredColor = ColorSensor.CpColor.GREEN;
                    break;
                case 'Y':
                    desiredColor = ColorSensor.CpColor.YELLOW;
                    break;
                default:
                    desiredColor = ColorSensor.CpColor.RED;
            }
        }

        int detectedIndex = Integer.MIN_VALUE;
        int desiredIndex = Integer.MIN_VALUE;
        for (int i = 0; i < colorVal.length; i++) {
            if (detectedColor.equals(colorVal[i])) {
                detectedIndex = i;
            }
            if(desiredColor.equals(colorVal[i])) {
                desiredIndex = i;
            }
        }

        // Determine which direction to move control panel
        if(detectedIndex - desiredIndex == 1 || detectedIndex - desiredIndex == -3) {
            talon.setOutput(1);
        } else if(detectedIndex - desiredIndex == 3 || detectedIndex - desiredIndex == -1) {
            talon.setOutput(-1);
        } else if(detectedIndex == desiredIndex) {
            talon.setOutput(0);
        } else {
            talon.setOutput(-1);
        }
        Debug.putNumber("Control Panel Slices", slices);
    }

    @Override
    public void update() {
        ColorSensor.CpColor detectedColor = colorSensor.getColor();

        if(controls.xbox.getXButton()) {
            rotationalControl(detectedColor);
        } else if(controls.xbox.getBButton()) {
            positionalControl(detectedColor);
        } else {
            talon.setOutput(0);
        }
        prevColor = detectedColor;
    }

    @Override
    public void reset() {
        slices = 0;
    }
}
