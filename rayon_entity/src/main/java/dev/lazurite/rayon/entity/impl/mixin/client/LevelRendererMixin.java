package dev.lazurite.rayon.entity.impl.mixin.client;

import com.jme3.math.Vector3f;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.lazurite.rayon.entity.api.EntityPhysicsElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

/**
 * This mixin allows the entity to be rendered in the correct location by
 * replacing the position of the entity with the position of the rigid body
 * (which is slightly different).
 * @see EntityRenderDispatcherMixin
 */
@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    //ModifyArgs has problem with modloader, for now, I have to use multiple ModifyArg
    //Also be sure not to confuse these 2, `ModifyArgs` and `ModifyArg` is different thing

    @Shadow @Final private Minecraft minecraft;

    @ModifyArg(
            method = "renderEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;render(Lnet/minecraft/world/entity/Entity;DDDFFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"
            ),
            index = 1
    )
    public double adjustCameraX(Entity entity, double x, double y, double z, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource source, int packedLightIn) {
        if (entity instanceof EntityPhysicsElement) {
            var location = ((EntityPhysicsElement) entity).getPhysicsLocation(new Vector3f(), partialTicks);
            var cameraPos = this.minecraft.gameRenderer.getMainCamera().getPosition();
            return location.x - cameraPos.x;
        }
        return x;
    }

    @ModifyArg(
            method = "renderEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;render(Lnet/minecraft/world/entity/Entity;DDDFFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"
            ),
            index = 2
    )
    public double adjustCameraY(Entity entity, double x, double y, double z, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource source, int packedLightIn) {
        if (entity instanceof EntityPhysicsElement) {
            var location = ((EntityPhysicsElement) entity).getPhysicsLocation(new Vector3f(), partialTicks);
            var cameraPos = this.minecraft.gameRenderer.getMainCamera().getPosition();
            return location.y - cameraPos.y;
        }
        return y;
    }

    @ModifyArg(
            method = "renderEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;render(Lnet/minecraft/world/entity/Entity;DDDFFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"
            ),
            index = 3
    )
    public double adjustCameraZ(Entity entity, double x, double y, double z, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource source, int packedLightIn) {
        if (entity instanceof EntityPhysicsElement) {
            var location = ((EntityPhysicsElement) entity).getPhysicsLocation(new Vector3f(), partialTicks);
            var cameraPos = this.minecraft.gameRenderer.getMainCamera().getPosition();
            return location.z - cameraPos.z;
        }
        return z;
    }
}
