package dev.lazurite.rayon.impl.mixin.client;

import dev.lazurite.rayon.Rayon;
import dev.lazurite.rayon.impl.util.thread.Clock;
import dev.lazurite.rayon.impl.util.config.Config;
import dev.lazurite.rayon.impl.physics.world.MinecraftDynamicsWorld;
import dev.lazurite.rayon.impl.mixin.common.IntegratedServerMixin;
import dev.lazurite.rayon.impl.mixin.common.ServerWorldMixin;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Unique private final Clock clock = new Clock();
    @Unique private float delta;

    @Shadow private Profiler profiler;
    @Shadow public ClientWorld world;

    /**
     * Steps the client {@link MinecraftDynamicsWorld}. Also handles if the game is paused.
     * @see IntegratedServerMixin
     * @see ServerWorldMixin
     */
    @Inject(
        method = "render(Z)V",
        at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/render/GameRenderer;render(FJZ)V",
                shift = At.Shift.AFTER
        )
    )
    private void render(boolean tick, CallbackInfo info) {
        if (world != null) {
            profiler.swap("physicsSimulation");
            BooleanSupplier shouldStep = () -> !((MinecraftClient) (Object) this).isPaused();
            float stepMillis = 1f / 60f;

            if (delta > stepMillis) {
                Rayon.WORLD.get(world).step(shouldStep);
                delta -= stepMillis;
                clock.get();
            } else {
                delta += clock.get();
            }
        }
    }
}