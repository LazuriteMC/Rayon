package dev.lazurite.rayon.entity.impl.mixin.common;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.entity.api.EntityPhysicsElement;
import dev.lazurite.rayon.core.impl.physics.space.body.ElementRigidBody;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Ticks {@link EntityPhysicsElement}s.
 */
@Mixin(ServerWorld.class)
public class ServerWorldMixin {
    @Inject(
            method = "tickEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;tick()V",
                    shift = At.Shift.AFTER
            )
    )
    public void tickEntity(Entity entity, CallbackInfo info) {
        if (entity instanceof EntityPhysicsElement) {
            ElementRigidBody body = ((EntityPhysicsElement) entity).getRigidBody();

            if (body.isInWorld()) {
                if (body.isActive() && body.getPriorityPlayer() == null && body.needsMovementUpdate()) {
                    ((EntityPhysicsElement) entity).sendMovementUpdate();
                }

                if (body.arePropertiesDirty()) {
                    ((EntityPhysicsElement) entity).sendProperties();
                }

                Vector3f location = body.getFrame().getLocation(new Vector3f(), 1.0f);
                float offset = body.getFrame().getBox(new BoundingBox(), 1.0f).getYExtent();
                entity.updatePosition(location.x, location.y - offset, location.z);
            }
        }
    }
}
