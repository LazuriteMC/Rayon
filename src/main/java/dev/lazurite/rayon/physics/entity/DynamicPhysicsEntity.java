package dev.lazurite.rayon.physics.entity;

import dev.lazurite.rayon.physics.Rayon;
import dev.lazurite.rayon.physics.helper.math.QuaternionHelper;
import dev.lazurite.rayon.physics.helper.math.VectorHelper;
import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

public class DynamicPhysicsEntity extends RigidBodyEntity implements ComponentV3, AutoSyncedComponent, CommonTickingComponent {
    public DynamicPhysicsEntity(@NotNull Entity entity) {
        super(entity);
    }

    public static DynamicPhysicsEntity get(Entity entity) {
        try {
            return Rayon.PHYSICS_ENTITY.get(entity);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void tick() {
//        Rayon.LOGGER.log(Level.INFO, getPosition());
        entity.pos = VectorHelper.vector3fToVec3d(getPosition());
    }

    @Override
    public void step(float delta) {

    }

    @Override
    public void readFromNbt(CompoundTag tag) {

    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        tag.put("orientation", QuaternionHelper.toTag(getOrientation()));
        tag.put("position", VectorHelper.toTag(getPosition()));
        tag.put("linear_velocity", VectorHelper.toTag(getLinearVelocity()));
        tag.put("angular_velocity", VectorHelper.toTag(getAngularVelocity()));
    }
}
