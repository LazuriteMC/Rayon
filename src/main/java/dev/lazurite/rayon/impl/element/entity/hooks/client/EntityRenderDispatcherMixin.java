package dev.lazurite.rayon.impl.element.entity.hooks.client;

import dev.lazurite.rayon.api.element.PhysicsElement;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Makes it so the built-in hitbox cannot be rendered for physics entities
 * since they get slightly messed up. Instead, you can press F3 + R to get
 * a more accurate hitbox of the physics entity.
 */
@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
    @Inject(method = "renderHitbox", at = @At("HEAD"), cancellable = true)
    private void renderHitbox(MatrixStack matrices, VertexConsumer vertices, Entity entity, float tickDelta, CallbackInfo info) {
        if (entity instanceof PhysicsElement) {
            info.cancel();
        }
    }
}
