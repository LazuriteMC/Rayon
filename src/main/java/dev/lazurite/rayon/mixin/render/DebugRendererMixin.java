package dev.lazurite.rayon.mixin.render;

import dev.lazurite.rayon.physics.PhysicsWorld;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.vecmath.Vector3f;

@Mixin(DebugRenderer.class)
public class DebugRendererMixin {
    @Inject(method = "render", at = @At("HEAD"))
    public void render(MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, double cameraX, double cameraY, double cameraZ, CallbackInfo info) {
        if (MinecraftClient.getInstance().options.debugEnabled) {
            Vector3f color = new Vector3f(1.0f, 0.0f, 0.0f); // red
            PhysicsWorld.INSTANCE.getDebugHelper().renderWorld(cameraX, cameraY, cameraZ, color, false);
        }
    }
}
