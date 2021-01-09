package dev.lazurite.rayon.mixin.client;

import dev.lazurite.rayon.util.config.Config;
import dev.lazurite.rayon.util.thread.Delta;
import dev.lazurite.rayon.physics.world.MinecraftDynamicsWorld;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
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
    @Unique private final Delta clock = new Delta();
    @Unique private float delta;

    @Shadow private Profiler profiler;
    @Shadow public ClientWorld world;

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("TAIL"))
    public void disconnect(Screen screen, CallbackInfo info) {
        Config.INSTANCE.isRemote = false;
    }

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

            if (Config.INSTANCE.stepRate < 260) {
                float stepMillis = 1 / (float) Config.INSTANCE.stepRate;

                if (delta > stepMillis) {
                    MinecraftDynamicsWorld.get(world).step(shouldStep);
                    delta -= stepMillis;
                    clock.get();
                } else {
                    delta += clock.get();
                }
            } else {
                MinecraftDynamicsWorld.get(world).step(shouldStep);
            }
        }
    }
}