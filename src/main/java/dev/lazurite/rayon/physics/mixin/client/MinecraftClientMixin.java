package dev.lazurite.rayon.physics.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Shadow private ClientWorld world;
    @Shadow private Profiler profiler;

    @Inject(
            method = "render(Z)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/GameRenderer;render(FJZ)V",
                    shift = At.Shift.AFTER
            )
    )
    private void render(boolean tick, CallbackInfo info) {
        if (this.world != null) {
            this.profiler.swap("physicsSimulation");
//            MinecraftDynamicsWorld.get(this.world).step();
        }
    }
}
