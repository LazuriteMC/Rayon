package dev.lazurite.rayon.impl.mixin.client.render;

import dev.lazurite.rayon.impl.util.debug.DebugManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.*;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * The main injection point for {@link DebugManager} to render to the screen.
 * @see DebugManager#render()
 */
@Mixin(DebugRenderer.class)
@Environment(EnvType.CLIENT)
public class DebugRendererMixin {
    @Inject(method = "render", at = @At("HEAD"))
    public void render(MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, double cameraX, double cameraY, double cameraZ, CallbackInfo info) {
        DebugManager.getInstance().render();
    }
}
