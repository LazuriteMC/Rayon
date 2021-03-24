package dev.lazurite.rayon.core.impl.physics.space.util;

public class BlockProperties {
    private final float friction;
    private final float restitution;
    private final boolean collidable;

    public BlockProperties(float friction, float restitution, boolean collidable) {
        this.friction = friction;
        this.restitution = restitution;
        this.collidable = collidable;
    }

    public BlockProperties(float friction, float restitution) {
        this(friction, restitution, true);
    }

    public float getFriction() {
        return this.friction;
    }

    public float getRestitution() {
        return this.restitution;
    }

    public boolean isCollidable() {
        return this.collidable;
    }
}
