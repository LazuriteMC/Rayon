package dev.lazurite.rayon.impl.mixin.client;

import dev.lazurite.rayon.Rayon;
import dev.lazurite.rayon.impl.physics.world.MinecraftDynamicsWorld;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * This mixin handles the joining of world physics threads
 * during the client disconnect phase.
 * @see MinecraftDynamicsWorld
 */
@Mixin(ClientWorld.class)
public class ClientWorldMixin {
    @Inject(method = "disconnect", at = @At("HEAD"))
    public void disconnect(CallbackInfo info) {
        Rayon.WORLD.get((World) (Object) this).destroy();
    }
}
