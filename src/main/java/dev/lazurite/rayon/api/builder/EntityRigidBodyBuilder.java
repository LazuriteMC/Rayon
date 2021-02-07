package dev.lazurite.rayon.api.builder;

import dev.lazurite.rayon.impl.builder.RigidBodyEntry;
import dev.lazurite.rayon.api.shape.EntityShapeFactory;
import dev.lazurite.rayon.impl.builder.RigidBodyBuilderImpl;
import net.minecraft.entity.Entity;

/**
 * The builder for creating a {@link RigidBodyEntry}.
 *
 * @param <E> should be your {@link Entity} class
 * @since 1.1.0
 * @see RigidBodyBuilderImpl
 * @see EntityRigidBodyRegistry
 */
public interface EntityRigidBodyBuilder<E extends Entity> {
    /**
     * Call this first to start creating your {@link RigidBodyEntry}.
     *
     * @param entityClass the class of your {@link Entity}
     * @param <E> should be your {@link Entity} class
     * @return a new {@link EntityRigidBodyBuilder} object which can be further configured before calling {@link EntityRigidBodyBuilder#build}
     */
    static <E extends Entity> RigidBodyBuilderImpl<E> create(Class<E> entityClass) {
        return new RigidBodyBuilderImpl<>(entityClass);
    }

    EntityRigidBodyBuilder<E> setShape(EntityShapeFactory factory);

    EntityRigidBodyBuilder<E> setMass(float mass);

    EntityRigidBodyBuilder<E> setDrag(float dragCoefficient);

    EntityRigidBodyBuilder<E> setFriction(float friction);

    EntityRigidBodyBuilder<E> setRestitution(float restitution);

    /**
     * Outputs a new {@link RigidBodyEntry}. This should be the
     * last thing called in your build chain.
     *
     * @return pass this result to {@link EntityRigidBodyRegistry#register}
     */
    RigidBodyEntry<E> build();
}
