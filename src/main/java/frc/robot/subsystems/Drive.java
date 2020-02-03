package frc.robot.subsystems;

import frc.robot.states.DriveState;
import frc.robot.states.RobotState;
import frc.util.*;
import frc.util.drivers.Limelight;
import frc.util.drivers.NavX;
import frc.util.drivers.Talon;
import frc.util.drivers.TalonFactory;

public class Drive implements Subsystem {
    private static Drive instance;
    public static Drive getInstance() {
        if(instance == null) {
            return instance = new Drive();
        } else {
            return instance;
        }
    }

    private Controls controls;
    private Limelight limelight;
    private DriveState driveState;
    private NavX navX;
    private PD visionDrive;

    // Master talons
    private Talon left;
    private Talon right;

    public Drive() {
        controls = Controls.getInstance();
        limelight = Limelight.getInstance();
        driveState = RobotState.getInstance().getDriveState();

        left = TalonFactory.masterTalon(ActuatorMap.leftFront, false);
        right = TalonFactory.masterTalon(ActuatorMap.leftFront, false);
        TalonFactory.slaveTalon(ActuatorMap.leftTop, true, left);
        TalonFactory.slaveTalon(ActuatorMap.leftBack, true, left);
        TalonFactory.slaveTalon(ActuatorMap.rightTop, false, right);
        TalonFactory.slaveTalon(ActuatorMap.rightBack, false, right);

        left.setName("Left Drive");
        right.setName("Right Drive");

        navX = NavX.getInstance();

        visionDrive = new PD(Constants.visionDriveP, Constants.visionDriveD);
    }

    @Override
    public void update() {
        driveState.updateAngle(getAngle());
        driveState.updateWheelPosition(getLeftRotations(), getRightRotations());
    }

    @Override
    public void reset() {
        left.reset();
        right.reset();
    }

    public void manualDrive() {
        if(controls.leftTrigger()) {
            visionDrive();
        } else {
            left.setOutput(controls.leftY());
            right.setOutput(controls.rightY());
        }
    }

    // Aim at the target
    public void visionDrive() {
        if(limelight.isValidTarget()) {
            double output = visionDrive.getOutput(limelight.getCenter().x);
            left.setOutput(controls.leftY() - output);
            right.setOutput(controls.leftY() + output);
        } else {
            setOutput(0, 0);
        }
    }

    public void setOutput(double l, double r) {
        left.setOutput(l);
        right.setOutput(r);
    }

    public double getLeftRotations() {
        return Conversion.encoderTicksToRotations(left.getSensor());
    }

    public double getRightRotations() {
        return Conversion.encoderTicksToRotations(right.getSensor());
    }

    public double getAngle() {
        if(Constants.usingTestBed) {
            return Debug.getNumber("navX angle");
        } else {
            return navX.getAngle();
        }
    }
}
