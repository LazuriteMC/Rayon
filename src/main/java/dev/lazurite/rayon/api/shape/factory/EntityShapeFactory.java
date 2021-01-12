package dev.lazurite.rayon.api.shape.factory;

import com.bulletphysics.collision.shapes.CollisionShape;
import dev.lazurite.rayon.api.registry.DynamicEntityRegistry;
import net.minecraft.entity.Entity;

/**
 * A factory for storing new shape definitions.
 * @since 1.0.0
 * @see DynamicEntityRegistry
 */
@FunctionalInterface
public interface EntityShapeFactory {
    CollisionShape create(Entity entity);
}
