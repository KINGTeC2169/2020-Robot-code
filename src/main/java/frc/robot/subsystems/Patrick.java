package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import frc.robot.commands.PatrickCommand;
import frc.util.ActuatorMap;
import frc.util.Constants;
import frc.util.Debug;
import frc.util.drivers.ColorSensor;
import frc.util.drivers.ControllerFactory;
import frc.util.drivers.DStation;

public class Patrick implements Subsystem {
    private static Patrick instance;
    public static Patrick getInstance(PatrickCommand pCommand) {
        if(!Constants.patrickEnabled) {
            return null;
        } else if(instance == null) {
            return instance = new Patrick(pCommand);
        } else {
            return instance;
        }
    }

    private final PatrickCommand pCommand;
    private final ColorSensor colorSensor;
    private TalonSRX talon;
    private int slices = 0;
    private ColorSensor.CpColor prevColor;
    private ColorSensor.CpColor desiredColor;
    private final ColorSensor.CpColor colorVal [] = {ColorSensor.CpColor.CYAN, ColorSensor.CpColor.YELLOW, ColorSensor.CpColor.RED, ColorSensor.CpColor.GREEN};

    public Patrick (PatrickCommand pCommand)
    {
        this.pCommand = pCommand;
        colorSensor = ColorSensor.getInstance();
        talon = ControllerFactory.masterTalon(ActuatorMap.patrick, false);
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
            talon.set(ControlMode.PercentOutput, 1);
        } else {
            talon.set(ControlMode.PercentOutput, 0);
        }
    }

    public void positionalControl(ColorSensor.CpColor detectedColor) {
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
            talon.set(ControlMode.PercentOutput, 1);
        } else if(detectedIndex - desiredIndex == 3 || detectedIndex - desiredIndex == -1) {
            talon.set(ControlMode.PercentOutput, -1);
        } else if(detectedIndex == desiredIndex) {
            talon.set(ControlMode.PercentOutput, 0);
        } else {
            talon.set(ControlMode.PercentOutput, -1);
        }
        Debug.putNumber("Control Panel Slices", slices);
    }

    @Override
    public void update() {
        ColorSensor.CpColor detectedColor = colorSensor.getColor();

        if(pCommand.isRotationalControl()) {
            rotationalControl(detectedColor);
        } else if(pCommand.isPositionalControl()) {
            positionalControl(detectedColor);
        } else {
            talon.set(ControlMode.PercentOutput, 0);
        }
        prevColor = detectedColor;
    }

    @Override
    public void reset() {
        slices = 0;
    }
}
