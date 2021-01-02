package dev.lazurite.rayon.api.shape;

import com.bulletphysics.collision.shapes.BoxShape;
import net.minecraft.entity.Entity;

import javax.vecmath.Vector3f;

public class EntityBoxShape extends BoxShape implements EntityShape {
    private final Entity entity;

    public EntityBoxShape(Entity entity) {
        super(new Vector3f(
                (float) (entity.getBoundingBox().maxX - entity.getBoundingBox().minX) / 2.0f,
                (float) (entity.getBoundingBox().maxY - entity.getBoundingBox().minY) / 2.0f,
                (float) (entity.getBoundingBox().maxZ - entity.getBoundingBox().minZ) / 2.0f));
        this.entity = entity;
    }

    @Override
    public Entity getEntity() {
        return this.entity;
    }
}
