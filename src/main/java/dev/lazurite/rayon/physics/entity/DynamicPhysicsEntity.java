package dev.lazurite.rayon.physics.entity;

import net.minecraft.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class DynamicPhysicsEntity extends RigidBodyEntity {
    public DynamicPhysicsEntity(@NotNull Entity entity) {
        super(entity);
    }

    @Override
    public void step(float delta) {

    }

//    @Override
//    public void readFromNbt(CompoundTag tag) {
//        super.readFromNbt(tag);
//    }
//
//    @Override
//    public void writeToNbt(CompoundTag tag) {
//        super.writeToNbt(tag);
//    }
}
