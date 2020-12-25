package dev.lazurite.rayon.mixin.render;

import dev.lazurite.rayon.physics.PhysicsWorld;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Used for hopping on into the render thread of the game.
 * @author Ethan Johnson
 */
@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Shadow @Final
    private MinecraftClient client;

    /**
     * Steps the physics world every frame.
     * @param tickDelta minecraft tick delta (0 - 1.0)
     * @param limitTime
     * @param matrix the {@link MatrixStack} used for performing transformations
     * @param info required by every mixin injection
     */
    @Inject(at = @At("HEAD"), method = "renderWorld")
    public void renderWorld(float tickDelta, long limitTime, MatrixStack matrix, CallbackInfo info) {
        PhysicsWorld.INSTANCE.stepWorld(client); // STEP
    }
}
