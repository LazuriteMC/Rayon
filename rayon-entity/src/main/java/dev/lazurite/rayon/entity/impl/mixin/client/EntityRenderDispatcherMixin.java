package dev.lazurite.rayon.entity.impl.mixin.client;

import com.jme3.math.Vector3f;
import dev.lazurite.rayon.core.impl.physics.debug.DebugManager;
import dev.lazurite.rayon.core.impl.physics.space.body.ElementRigidBody;
import dev.lazurite.rayon.entity.api.EntityPhysicsElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Corrects the positions of shadows and debug hitboxes.
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

    @Inject(method = "renderHitbox", at = @At("HEAD"), cancellable = true)
    private static void renderHitbox(MatrixStack matrices, VertexConsumer vertices, Entity entity, float tickDelta, CallbackInfo info) {
        if (entity instanceof EntityPhysicsElement) {
            ElementRigidBody rigidBody = ((EntityPhysicsElement) entity).getRigidBody();
            Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
            DebugManager.getInstance().renderBody(rigidBody, matrices, camera, 2.0f, tickDelta, false);
            info.cancel();
        }
    }
}
