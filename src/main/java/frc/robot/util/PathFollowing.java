package frc.robot.util;

import frc.robot.states.DriveState;
import frc.robot.util.geometry.Vector2;

public class PathFollowing {
    /* To follow paths, we use a coordinate system where:
    * 1. The origin is the top left corner of the robot
    * 2. The robot always has a velocity vector of <0, 1> */

    public static Vector2 getOutputs(DriveState driveState, Vector2 dest, Vector2 destVel) {
        Vector2 driveVel = driveState.getFrontLeftVelocity();
        Vector2 s = dest.sub(driveState.getFrontLeft()); // Goal position
        Vector2 v = destVel.scale(1 / driveVel.norm()).rotate(Math.PI / 2 - driveVel.angle()); // Goal velocity
        int c = getCase(s, v);

        switch(c) {
            case 1: // TODO
                // Accelerate until we're near the target, then slow down.
                return null;
            case 2: // TODO
                // Turn in the direction that makes v more vertical
                return null;
            case 3: // TODO
                // Drive straight. Decelerate if predicted curvature is too great
                return null;
            case 4: // TODO
                // Turn in the direction that makes v less vertical to gain a better position
                return null;
        }
        return new Vector2(0, 0);
    }

    public static int getCase(Vector2 s, Vector2 v) {
        // Case 1: We're on track to hit the target
        double positionAngle = s.angle();
        double velocityAngle = v.angle();
        if(
                positionAngle > Math.PI * 17 / 36 && positionAngle < Math.PI * 19 / 36 && // Position is 85-95 degrees
                velocityAngle > Math.PI * 17 / 36 && velocityAngle < Math.PI * 19 / 36    // Velocity is 85-95 degrees
        ) {
            return 1;
        }

        // Case 2: We can get on track with a little angle adjustment
        if(true /*TODO*/) {
            return 2;
        }

        // Case 3: We need to keep driving straight before we adjust our angle
        if(true /*TODO*/) {
            return 3;
        }

        // Case 4: We can't hit the target by reducing velocity angle error or driving straight
        return 4;
    }
}
