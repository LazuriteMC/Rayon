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
    /* I blame forgay */

    @Shadow public Camera camera;
    private Entity entity;
    private float tickDelta;

    @Inject(method = "render", at = @At("HEAD"))
    public void render(Entity entity, double d, double e, double f, float g, float h, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo info) {
        this.entity = entity;
        this.tickDelta = h;
    }

    @ModifyVariable(method = "render", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    public double renderX(double d) {
        if (this.entity instanceof EntityPhysicsElement element) {
            final var location = element.getPhysicsLocation(new Vector3f(), this.tickDelta);
            final var cameraPos = camera.getPosition();
            return location.x - cameraPos.x;
        }

        return d;
    }

    @ModifyVariable(method = "render", at = @At("HEAD"), ordinal = 1, argsOnly = true)
    public double renderY(double e) {
        if (this.entity instanceof EntityPhysicsElement element) {
            final var location = element.getPhysicsLocation(new Vector3f(), this.tickDelta);
            final var cameraPos = camera.getPosition();
            return location.y - cameraPos.y;
        }

        return e;
    }

    @ModifyVariable(method = "render", at = @At("HEAD"), ordinal = 2, argsOnly = true)
    public double renderZ(double f) {
        if (this.entity instanceof EntityPhysicsElement element) {
            final var location = element.getPhysicsLocation(new Vector3f(), this.tickDelta);
            final var cameraPos = camera.getPosition();
            return location.z - cameraPos.z;
        }

        return f;
    }

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
