package dev.lazurite.rayon.physics.shape.entity;

import dev.lazurite.rayon.api.shape.EntityShape;
import dev.lazurite.rayon.physics.shape.BoundingBoxShape;
import net.minecraft.entity.Entity;

public class EntityBoxShape extends BoundingBoxShape implements EntityShape {
    private final Entity entity;

    public EntityBoxShape(Entity entity) {
        super(entity.getBoundingBox());
        this.entity = entity;
    }

    @Override
    public Entity getEntity() {
        return this.entity;
    }
}
