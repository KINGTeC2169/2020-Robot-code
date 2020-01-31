package frc.robot.util.geometry;

public class Pos2 {
    private Vector2 translation;
    private Rotation2 rotation;

    public Pos2() {
        translation = new Vector2();
        rotation = new Rotation2();
    }

    public Pos2(Vector2 translation, Rotation2 rotation) {
        this.translation = translation;
        this.rotation = rotation;
    }

    public Vector2 getTranslation() {
        return translation;
    }

    public Rotation2 getRotation() {
        return rotation;
    }
}
