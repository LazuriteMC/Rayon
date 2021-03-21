package dev.lazurite.rayon.entity.impl.mixin.client.world;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.entity.api.EntityPhysicsElement;
import dev.lazurite.rayon.core.impl.thread.space.body.ElementRigidBody;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Tick {@link EntityPhysicsElement}s.
 */
@Mixin(ClientWorld.class)
public class ClientWorldMixin {
    @Shadow @Final private MinecraftClient client;

    @Inject(method = "tickEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;tick()V"))
    public void tickEntity(Entity entity, CallbackInfo info) {
        if (entity instanceof EntityPhysicsElement) {
            ElementRigidBody body = ((EntityPhysicsElement) entity).getRigidBody();

            if (body.isInWorld()) {
                if (body.isActive() && client.player.equals(body.getPriorityPlayer())) {
                    ((EntityPhysicsElement) entity).sendMovementUpdate();
                }

                Vector3f pos = body.getPhysicsLocation(new Vector3f());
                entity.updatePosition(pos.x, pos.y - body.boundingBox(new BoundingBox()).getYExtent(), pos.z);
            }
        }
    }
}
