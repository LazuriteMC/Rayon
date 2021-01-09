package dev.lazurite.rayon.mixin.client.render;

import dev.lazurite.rayon.physics.world.MinecraftDynamicsWorld;
import dev.lazurite.rayon.physics.world.DebuggableDynamicsWorld;
import dev.lazurite.rayon.physics.helper.DebugHelper;
import dev.lazurite.rayon.util.config.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * This mixin calls {@link DebugHelper#renderWorld(double, double, double, boolean)} in
 * order to draw rigid body outlines to the screen.
 * @see DebugHelper
 * @see DebuggableDynamicsWorld
 */
@Mixin(DebugRenderer.class)
public class DebugRendererMixin {
    @Inject(method = "render", at = @At("HEAD"))
    public void render(MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, double cameraX, double cameraY, double cameraZ, CallbackInfo info) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (Config.INSTANCE.debug) {
            MinecraftDynamicsWorld.get(client.world).getDebugHelper().renderWorld(cameraX, cameraY, cameraZ, Config.INSTANCE.debugBlocks);
        }
    }
}
