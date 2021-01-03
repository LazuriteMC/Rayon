package dev.lazurite.rayon.physics.shape.entity;

import dev.lazurite.rayon.api.shape.EntityShape;
import dev.lazurite.rayon.physics.shape.BakedModelShape;
import net.minecraft.entity.Entity;

public class EntityModelShape extends BakedModelShape implements EntityShape {
    private final Entity entity;

    public EntityModelShape(Entity entity) {
        super(null); // TODO figure this out
        this.entity = entity;
    }

    @Override
    public Entity getEntity() {
        return this.entity;
    }
}
