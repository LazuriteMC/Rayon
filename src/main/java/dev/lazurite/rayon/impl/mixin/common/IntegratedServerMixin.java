package dev.lazurite.rayon.impl.mixin.common;

import dev.lazurite.rayon.impl.physics.world.MinecraftDynamicsWorld;
import dev.lazurite.rayon.impl.mixin.common.world.ServerWorldMixin;
import dev.lazurite.rayon.impl.mixin.client.MinecraftClientMixin;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.dedicated.DedicatedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

/**
 * If the game is paused, it's important to clear all delta time
 * within {@link MinecraftDynamicsWorld}. Otherwise, dynamic entities
 * tend to go flying or fall out of the world.<br><br>
 *
 * Since a normal {@link DedicatedServer} is unable to pause, but an
 * {@link IntegratedServer} can, this mixin only affects the
 * {@link IntegratedServer} class.<br><br>
 *
 * @see ServerWorldMixin
 * @see MinecraftClientMixin
 */
@Mixin(IntegratedServer.class)
public abstract class IntegratedServerMixin {
    @Shadow private boolean paused;

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(BooleanSupplier shouldKeepTicking, CallbackInfo info) {
        if (paused) {
            ((IntegratedServer) (Object) this).getWorlds().forEach(world -> MinecraftDynamicsWorld.get(world).getClock().reset());
        }
    }
}
