package dev.lazurite.rayon.api.builder;

import com.jme3.bullet.collision.shapes.CollisionShape;
import dev.lazurite.rayon.impl.builder.RigidBodyEntry;
import dev.lazurite.rayon.api.shape.factory.EntityShapeFactory;
import dev.lazurite.rayon.impl.builder.RigidBodyBuilderImpl;
import net.minecraft.entity.Entity;

/**
 * The builder for creating a {@link RigidBodyEntry}.
 *
 * @param <E> should be your {@link Entity} class
 * @since 1.1.0
 * @see RigidBodyBuilderImpl
 * @see RigidBodyRegistry
 */
public interface RigidBodyBuilder<E extends Entity> {
    /**
     * Call this first to start creating your {@link RigidBodyEntry}.
     *
     * @param entityClass the class of your {@link Entity}
     * @param shapeFactory the {@link EntityShapeFactory} for creating a {@link CollisionShape}
     * @param <E> should be your {@link Entity} class
     * @return a new {@link RigidBodyBuilder} object which can be further configured before calling {@link RigidBodyBuilder#build}
     */
    static <E extends Entity> RigidBodyBuilderImpl<E> create(Class<E> entityClass, EntityShapeFactory shapeFactory) {
        return new RigidBodyBuilderImpl<>(entityClass, shapeFactory);
    }

    RigidBodyBuilder<E> setMass(float mass);

    RigidBodyBuilder<E> setDrag(float dragCoefficient);

    RigidBodyBuilder<E> setFriction(float friction);

    RigidBodyBuilder<E> setRestitution(float restitution);

    /**
     * Outputs a new {@link RigidBodyEntry}. This should be the
     * last thing called in your build chain.
     *
     * @return pass this result to {@link RigidBodyRegistry#register}
     */
    RigidBodyEntry<E> build();
}
