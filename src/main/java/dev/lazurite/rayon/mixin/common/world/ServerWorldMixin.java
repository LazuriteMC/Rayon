package dev.lazurite.rayon.mixin.common.world;

import dev.lazurite.rayon.physics.world.MinecraftDynamicsWorld;
import dev.lazurite.rayon.mixin.common.IntegratedServerMixin;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

/**
 * This simply calls {@link MinecraftDynamicsWorld#step} each time the server ticks.
 * @see MinecraftDynamicsWorld#step
 * @see IntegratedServerMixin
 */
@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {
    @Inject(
            method = "tick(Ljava/util/function/BooleanSupplier;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/world/ServerChunkManager;tick(Ljava/util/function/BooleanSupplier;)V",
                    shift = At.Shift.AFTER
            )
    )
    public void tick(BooleanSupplier shouldKeepTicking, CallbackInfo info) {
        ((World) (Object) this).getProfiler().swap("physicsSimulation");
        MinecraftDynamicsWorld.get((ServerWorld) (Object) this).step(shouldKeepTicking);
    }
}
