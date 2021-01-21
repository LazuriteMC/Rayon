package dev.lazurite.rayon.impl.builder;

import dev.lazurite.rayon.api.builder.RigidBodyBuilder;
import dev.lazurite.rayon.api.shape.factory.EntityShapeFactory;
import net.minecraft.entity.Entity;

public class RigidBodyBuilderImpl<E extends Entity> implements RigidBodyBuilder<E> {
    private final Class<E> entityClass;
    private final EntityShapeFactory shapeFactory;
    private float mass;
    private float dragCoefficient;
    private float friction;
    private float restitution;

    public RigidBodyBuilderImpl(Class<E> entityClass, EntityShapeFactory shapeFactory) {
        this.entityClass = entityClass;
        this.shapeFactory = shapeFactory;
        this.dragCoefficient = 0.05f;
        this.mass = 1.0f;
        this.friction = 1.0f;
        this.restitution = 0.0f;
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
