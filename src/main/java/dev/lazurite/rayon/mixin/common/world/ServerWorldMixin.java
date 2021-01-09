package dev.lazurite.rayon.mixin.common.world;

import dev.lazurite.rayon.util.thread.ClientState;
import dev.lazurite.rayon.physics.world.MinecraftDynamicsWorld;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World {

    /* Ughhhh */
    protected ServerWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, DimensionType dimensionType, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long seed) {
        super(properties, registryRef, dimensionType, profiler, isClient, debugWorld, seed);
    }

    @Inject(
            method = "tick(Ljava/util/function/BooleanSupplier;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/world/ServerChunkManager;tick(Ljava/util/function/BooleanSupplier;)V",
                    shift = At.Shift.AFTER
            )
    )
    public void tick(BooleanSupplier shouldKeepTicking, CallbackInfo info) {
        this.getProfiler().swap("physicsSimulation");

        BooleanSupplier shouldStep = () -> {
            if (getServer() instanceof IntegratedServer) {
                return !ClientState.isPaused();
            } else {
                return true;
            }
        };

        MinecraftDynamicsWorld.get(this).step(shouldStep);
    }
}
