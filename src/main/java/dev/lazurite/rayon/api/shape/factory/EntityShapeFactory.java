package dev.lazurite.rayon.api.shape.factory;

import com.bulletphysics.collision.shapes.CollisionShape;
import net.minecraft.entity.Entity;

@FunctionalInterface
public interface EntityShapeFactory {
    CollisionShape create(Entity entity);
}
