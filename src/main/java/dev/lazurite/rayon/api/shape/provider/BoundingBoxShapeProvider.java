package dev.lazurite.rayon.api.shape.provider;

import dev.lazurite.rayon.physics.shape.BoundingBoxShape;
import net.minecraft.entity.Entity;
import net.minecraft.util.shape.VoxelShape;

public interface BoundingBoxShapeProvider {
    static BoundingBoxShape get(Entity entity) {
        return new BoundingBoxShape(entity.getBoundingBox());
    }

    static BoundingBoxShape get(VoxelShape voxelShape) {
        if (!voxelShape.isEmpty()) {
            return new BoundingBoxShape(voxelShape.getBoundingBox());
        }

        return null;
    }
}
