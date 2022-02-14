package dev.lazurite.rayon.impl.mixin.client;

import com.jme3.math.Vector3f;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.lazurite.rayon.api.EntityPhysicsElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Redirect(
            method = "renderEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;render(Lnet/minecraft/world/entity/Entity;DDDFFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"
            )
    )
    public void renderEntity_render(EntityRenderDispatcher dispatcher, Entity entity, double d, double e, double f, float g, float h, PoseStack poseStack, MultiBufferSource multiBufferSource, int i) {
        if (entity instanceof EntityPhysicsElement element) {
            final var cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
            final var location = element.getPhysicsLocation(new Vector3f(), h);
            dispatcher.render(entity, location.x - cameraPos.x, location.y - cameraPos.y, location.z - cameraPos.z, g, h, poseStack, multiBufferSource, i);
        }

        dispatcher.render(entity, d, e, f, g, h, poseStack, multiBufferSource, i);
    }

}
