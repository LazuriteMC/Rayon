package dev.lazurite.rayon.impl.mixin.client.render;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.api.element.PhysicsElement;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
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
    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/util/math/MatrixStack;translate(DDD)V",
                    shift = At.Shift.AFTER,
                    ordinal = 1
            )
    )
    public <E extends Entity> void render(E entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo info) {
        if (entity instanceof PhysicsElement) {
            Vector3f pos = ((PhysicsElement) entity).getPhysicsLocation(new Vector3f(), tickDelta);
            float a = pos.x - (float) MathHelper.lerp(tickDelta, entity.lastRenderX, entity.getX());
            float b = pos.y - (float) MathHelper.lerp(tickDelta, entity.lastRenderY, entity.getY()) -
                    ((PhysicsElement) entity).getRigidBody().boundingBox(new BoundingBox()).getYExtent() * 2;
            float c = pos.z - (float) MathHelper.lerp(tickDelta, entity.lastRenderZ, entity.getZ());

            /* Translate to center of entity position rather than rigid body position */
            matrices.translate(a, b, c);
        }
    }

    @ModifyVariable(
            method = "renderShadow",
            at = @At(
                    value = "STORE",
                    opcode = Opcodes.DSTORE
            ),
            ordinal = 1
    )
    private static double e(double e, MatrixStack stack, VertexConsumerProvider provider, Entity entity) {
        if (entity instanceof PhysicsElement) {
            return e - ((PhysicsElement) entity).getRigidBody().boundingBox(new BoundingBox()).getYExtent() * 2;
        }

        return e;
    }
}
