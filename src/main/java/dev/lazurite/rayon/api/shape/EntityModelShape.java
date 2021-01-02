package dev.lazurite.rayon.api.shape;

import dev.lazurite.rayon.physics.util.shape.BakedModelShape;
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
