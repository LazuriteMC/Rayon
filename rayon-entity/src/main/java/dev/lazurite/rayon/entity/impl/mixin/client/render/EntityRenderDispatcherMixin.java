package dev.lazurite.rayon.entity.impl.mixin.client.render;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.entity.api.EntityPhysicsElement;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Makes it so the built-in hitbox renders in the correct position as well as shadows.
 * @see WorldRendererMixin
 */
@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {
    @ModifyVariable(
            method = "renderShadow",
            at = @At(value = "STORE", opcode = Opcodes.DSTORE),
            ordinal = 1
    )
    private static double renderShadowY(double e, MatrixStack stack, VertexConsumerProvider provider, Entity entity, float opacity, float tickDelta) {
        if (entity instanceof EntityPhysicsElement) {
            return ((EntityPhysicsElement) entity).getPhysicsLocation(new Vector3f(), tickDelta).y;
        }

        return e;
    }

    @Inject(method = "drawBox", at = @At("HEAD"), cancellable = true)
    private void drawBox(MatrixStack matrix, VertexConsumer vertices, Entity entity, float red, float green, float blue, CallbackInfo info) {
        if (entity instanceof EntityPhysicsElement) {
            info.cancel();
        }
    }

    @Inject(method = "renderHitbox", at = @At("HEAD"), cancellable = true)
    private void renderHitbox(MatrixStack matrices, VertexConsumer vertices, Entity entity, float tickDelta, CallbackInfo info) {
        if (entity instanceof EntityPhysicsElement) {
            Box box = entity.getBoundingBox().offset(-entity.getX(),
                    -entity.getY() - ((EntityPhysicsElement) entity).getRigidBody().boundingBox(new BoundingBox()).getYExtent(), -entity.getZ());
            WorldRenderer.drawBox(matrices, vertices, box, 1.0f, 1.0f, 1.0f, 1.0F);
            info.cancel();
        }
    }
}
