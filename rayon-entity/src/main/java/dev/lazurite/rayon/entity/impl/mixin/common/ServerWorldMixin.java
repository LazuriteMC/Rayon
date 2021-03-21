package dev.lazurite.rayon.entity.impl.mixin.common;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.entity.api.EntityPhysicsElement;
import dev.lazurite.rayon.core.impl.thread.space.body.ElementRigidBody;
import dev.lazurite.rayon.entity.impl.util.ElementPropertiesS2C;
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
                    target = "Lnet/minecraft/entity/Entity;tick()V"
            )
    )
    public void tickEntity(Entity entity, CallbackInfo info) {
        if (entity instanceof EntityPhysicsElement) {
            ElementRigidBody body = ((EntityPhysicsElement) entity).getRigidBody();

            if (body.isInWorld()) {
                if (body.isActive() && body.getPriorityPlayer() == null) {
                    ((EntityPhysicsElement) entity).sendMovementUpdate();
                }

                if (body.arePropertiesDirty()) {
                    ElementPropertiesS2C.send((EntityPhysicsElement) entity);
                    body.setPropertiesDirty(false);
                }

                Vector3f pos = body.getPhysicsLocation(new Vector3f());
                entity.updatePosition(pos.x, pos.y - body.boundingBox(new BoundingBox()).getYExtent(), pos.z);
            }
        }
    }
}
