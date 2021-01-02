package dev.lazurite.rayon.api.shape.factory;

import dev.lazurite.rayon.api.shape.EntityShape;
import net.minecraft.entity.Entity;

public interface EntityShapeFactory<S extends EntityShape> {
    S create(Entity entity);
}
