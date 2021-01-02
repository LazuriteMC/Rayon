package dev.lazurite.rayon.physics.mixin.client;

import dev.lazurite.rayon.physics.world.MinecraftDynamicsWorld;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Shadow @Final private MinecraftClient client;

    @Inject(method = "renderWorld", at = @At("HEAD"))
    public void renderWorld(float tickDelta, long limitTime, MatrixStack matrix, CallbackInfo info) {
        MinecraftDynamicsWorld.get(client.world).step();
    }
}
