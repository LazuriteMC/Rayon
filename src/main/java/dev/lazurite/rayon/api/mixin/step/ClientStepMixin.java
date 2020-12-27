package dev.lazurite.rayon.api.mixin.step;

import dev.lazurite.rayon.api.Rayon;
import dev.lazurite.rayon.api.physics.entity.PhysicsEntityComponent;
import dev.lazurite.rayon.api.physics.util.Delta;
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
        float delta = clock.get();
        Rayon.PHYSICS_WORLD.get(client.world).step(delta);

        for (Entity entity : client.world.getEntities()) {
            PhysicsEntityComponent component = PhysicsEntityComponent.get(entity);

            if (component != null) {
                Rayon.PHYSICS_ENTITY.get(entity).step(delta);
            }
        }
    }
}
