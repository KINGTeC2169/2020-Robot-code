package frc.util.drivers;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import frc.util.ActuatorMap;

import java.io.IOException;

public class DSolenoid {

    private DoubleSolenoid sol;

    public DSolenoid(int input, int output){


        try {
            sol = new DoubleSolenoid(ActuatorMap.pcm, input, output);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setName(String s){

    }

    public void set(boolean extended){
        if(extended){
            sol.set(DoubleSolenoid.Value.kForward);
        }
        else{
            sol.set(DoubleSolenoid.Value.kReverse);
        }
    }

}
