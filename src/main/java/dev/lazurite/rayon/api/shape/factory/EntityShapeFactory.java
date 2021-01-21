package dev.lazurite.rayon.api.shape.factory;

import com.jme3.bullet.collision.shapes.CollisionShape;
import dev.lazurite.rayon.api.builder.RigidBodyRegistry;
import net.minecraft.entity.Entity;

/**
 * A factory for storing new shape definitions.
 *
 * @since 1.0.0
 * @see RigidBodyRegistry
 */
@FunctionalInterface
public interface EntityShapeFactory {
    CollisionShape create(Entity entity);
}
