package dev.lazurite.rayon.mixin.step;

import dev.lazurite.rayon.physics.entity.DynamicEntityPhysics;
import dev.lazurite.rayon.physics.util.Delta;
import dev.lazurite.rayon.physics.world.MinecraftDynamicsWorld;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Used for hopping on into the render thread of the game.
 * @author Ethan Johnson
 */
@Mixin(GameRenderer.class)
public class ClientStepMixin {
    @Unique private final Delta clock = new Delta();
    @Shadow @Final private MinecraftClient client;

    /**
     * Steps the physics world every frame.
     * @param tickDelta minecraft tick delta (0 - 1.0)
     * @param limitTime
     * @param matrix the {@link MatrixStack} used for performing transformations
     * @param info required by every mixin injection
     */
    @Inject(at = @At("TAIL"), method = "renderWorld")
    public void renderWorld(float tickDelta, long limitTime, MatrixStack matrix, CallbackInfo info) {
        if (!client.isPaused()) {
            /* Get the world component */
            MinecraftDynamicsWorld dynamicsWorld = MinecraftDynamicsWorld.get(client.world);

            /* Get delta */
            float delta = clock.get();

            /* Step the client world */
            MinecraftDynamicsWorld.get(client.world).step(delta);

            for (Entity entity : dynamicsWorld.getEntities()) {
                DynamicEntityPhysics.get(entity).step(delta);
            }
        }
    }
}
