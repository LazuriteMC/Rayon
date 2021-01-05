package dev.lazurite.rayon.api.shape.provider;

import dev.lazurite.rayon.physics.shape.BakedModelShape;
import net.minecraft.entity.Entity;

public interface BakedModelShapeProvider {
    static BakedModelShape get(Entity entity) {
        return new BakedModelShape(null); // TODO
    }

//    static BakedModelShape get(VoxelShape voxelShape) {
//        if (!voxelShape.isEmpty()) {
//            return new BakedModelShape(voxelShape);
//        }
//
//        return null;
//    }
}
