package dev.lazurite.rayon.impl.mixin.client;

import com.jme3.math.Vector3f;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.lazurite.rayon.api.EntityPhysicsElement;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Corrects the positions of shadows and debug hitboxes.
 */
@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {
    @ModifyVariable(
            method = "renderShadow",
            at = @At(value = "STORE", opcode = Opcodes.DSTORE),
            ordinal = 1
    )
    private static double renderShadowY(double e, PoseStack matrices, MultiBufferSource provider, Entity entity, float opacity, float tickDelta) {
        if (entity instanceof EntityPhysicsElement) {
            return ((EntityPhysicsElement) entity).getPhysicsLocation(new Vector3f(), tickDelta).y;
        }

        return e;
    }

    @Inject(method = "renderHitbox", at = @At("HEAD"), cancellable = true)
    private static void renderHitbox(PoseStack matrices, VertexConsumer vertices, Entity entity, float tickDelta, CallbackInfo info) {
        if (entity instanceof EntityPhysicsElement) {
            info.cancel();
        }
    }
}
