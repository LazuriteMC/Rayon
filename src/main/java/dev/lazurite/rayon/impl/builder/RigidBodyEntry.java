package dev.lazurite.rayon.impl.builder;

import dev.lazurite.rayon.api.builder.RigidBodyRegistry;
import dev.lazurite.rayon.api.shape.factory.EntityShapeFactory;
import net.minecraft.entity.Entity;

/**
 * A container to house all the data for each registered entity.
 * @param <E> the specific type of {@link Entity}
 * @see RigidBodyRegistry#register
 */
public class RigidBodyEntry<E extends Entity> {
    private final Class<E> entity;
    private final EntityShapeFactory shapeFactory;
    private final float mass;
    private final float dragCoefficient;
    private final float friction;
    private final float restitution;

    public RigidBodyEntry(Class<E> entity, EntityShapeFactory shapeFactory, float mass, float dragCoefficient, float friction, float restitution) {
        this.entity = entity;
        this.shapeFactory = shapeFactory;
        this.mass = mass;
        this.dragCoefficient = dragCoefficient;
        this.friction = friction;
        this.restitution = restitution;
    }

    public Class<E> getEntity() {
        return this.entity;
    }

    public EntityShapeFactory getShapeFactory() {
        return this.shapeFactory;
    }

    public float getMass() {
        return this.mass;
    }

    public float getDragCoefficient() {
        return this.dragCoefficient;
    }

    public float getFriction() {
        return this.friction;
    }

    public float getRestitution() {
        return this.restitution;
    }
}
