package dev.lazurite.rayon.impl.builder;

import dev.lazurite.rayon.api.builder.RigidBodyBuilder;
import dev.lazurite.rayon.api.builder.RigidBodyRegistry;
import dev.lazurite.rayon.api.shape.EntityShapeFactory;
import dev.lazurite.rayon.api.shape.BoundingBoxShape;
import net.minecraft.entity.Entity;

/**
 * The implementation of {@link RigidBodyBuilder}. Mainly just houses the
 * rigid body information until it is compiled into a {@link RigidBodyEntry}
 * object and passed to {@link RigidBodyRegistry}.
 *
 * @param <E> should be your {@link Entity} class
 * @see RigidBodyRegistry
 * @see RigidBodyBuilder
 */
public class RigidBodyBuilderImpl<E extends Entity> implements RigidBodyBuilder<E> {
    private final Class<E> entityClass;
    private EntityShapeFactory shapeFactory = BoundingBoxShape.getFactory();
    private float mass = 1.0f;
    private float dragCoefficient = 0.05f;
    private float friction = 1.0f;
    private float restitution = 0.0f;

    public RigidBodyBuilderImpl(Class<E> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public RigidBodyBuilder<E> setShape(EntityShapeFactory shapeFactory) {
        this.shapeFactory = shapeFactory;
        return this;
    }

    @Override
    public RigidBodyBuilder<E> setMass(float mass) {
        this.mass = mass;
        return this;
    }

    @Override
    public RigidBodyBuilder<E> setDrag(float dragCoefficient) {
        this.dragCoefficient = dragCoefficient;
        return this;
    }

    @Override
    public RigidBodyBuilder<E> setFriction(float friction) {
        this.friction = friction;
        return this;
    }

    @Override
    public RigidBodyBuilder<E> setRestitution(float restitution) {
        this.restitution = restitution;
        return this;
    }

    @Override
    public RigidBodyEntry<E> build() {
        return new RigidBodyEntry<>(entityClass, shapeFactory, mass, dragCoefficient, friction, restitution);
    }
}
