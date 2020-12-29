package dev.lazurite.rayon.physics.util;

import javax.vecmath.Vector3f;

public enum BodyType {
    ENTITY(new Vector3f(1, 0, 0)),
    BLOCK(new Vector3f(0, 0, 1));

    private final Vector3f color;

    BodyType(Vector3f color) {
        this.color = color;
    }

    public Vector3f getColor() {
        return this.color;
    }
}
