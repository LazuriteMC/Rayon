package dev.lazurite.rayon.mixin;

import dev.lazurite.rayon.client.PhysicsWorld;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(at = @At("HEAD"), method = "renderWorld")
    public void renderWorld(float tickDelta, long limitTime, MatrixStack matrix, CallbackInfo info) {
        PhysicsWorld.getInstance().stepWorld();
    }
}
