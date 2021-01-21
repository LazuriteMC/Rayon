package dev.lazurite.rayon.api.builder;

import dev.lazurite.rayon.impl.builder.RigidBodyEntry;
import dev.lazurite.rayon.api.shape.factory.EntityShapeFactory;
import dev.lazurite.rayon.impl.builder.RigidBodyBuilderImpl;
import net.minecraft.entity.Entity;

public interface RigidBodyBuilder<E extends Entity> {
    static <E extends Entity> RigidBodyBuilderImpl<E> create(Class<E> entityType, EntityShapeFactory shapeFactory) {
        return new RigidBodyBuilderImpl<>(entityType, shapeFactory);
    }

    RigidBodyBuilder<E> setMass(float mass);
    RigidBodyBuilder<E> setDrag(float dragCoefficient);
    RigidBodyBuilder<E> setFriction(float friction);
    RigidBodyBuilder<E> setRestitution(float restitution);
    RigidBodyEntry<E> build();
}
