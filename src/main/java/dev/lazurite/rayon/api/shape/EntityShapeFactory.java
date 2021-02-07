package dev.lazurite.rayon.api.shape;

import com.jme3.bullet.collision.shapes.CollisionShape;
import dev.lazurite.rayon.api.builder.EntityRigidBodyRegistry;
import net.minecraft.entity.Entity;

/**
 * A factory for storing new shape definitions.
 *
 * @since 1.0.0
 * @see EntityRigidBodyRegistry
 */
@FunctionalInterface
public interface EntityShapeFactory {
    CollisionShape create(Entity entity);
}
