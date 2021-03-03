package dev.lazurite.rayon.impl.element.entity.hooks.render;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.api.element.PhysicsElement;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Makes it so the built-in hitbox renders in the correct position.
 * @see WorldRendererMixin
 */
@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
    @Inject(method = "renderHitbox", at = @At("HEAD"), cancellable = true)
    private void renderHitbox(MatrixStack matrices, VertexConsumer vertices, Entity entity, float tickDelta, CallbackInfo info) {
        if (entity instanceof PhysicsElement) {
            Vector3f pos = ((PhysicsElement) entity).getPhysicsLocation(new Vector3f(), tickDelta);
            float x = pos.x - (float) MathHelper.lerp(tickDelta, entity.lastRenderX, entity.getX());
            float y = pos.y - (float) MathHelper.lerp(tickDelta, entity.lastRenderY, entity.getY()) -
                    ((PhysicsElement) entity).getRigidBody().boundingBox(new BoundingBox()).getYExtent() * 2;
            float z = pos.z - (float) MathHelper.lerp(tickDelta, entity.lastRenderZ, entity.getZ());
            matrices.translate(x, y, z);
        }
    }
}
