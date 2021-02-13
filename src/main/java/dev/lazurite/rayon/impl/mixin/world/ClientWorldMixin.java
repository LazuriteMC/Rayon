package dev.lazurite.rayon.impl.mixin.world;

import dev.lazurite.rayon.impl.Rayon;
import dev.lazurite.rayon.impl.bullet.space.MinecraftSpace;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * This mixin handles the joining of world physics threads
 * during the client disconnect phase.
 * @see MinecraftSpace
 */
@Mixin(ClientWorld.class)
public class ClientWorldMixin {
    @Inject(method = "disconnect", at = @At("HEAD"))
    public void disconnect(CallbackInfo info) {
        Rayon.THREAD.get((World) (Object) this).execute(MinecraftSpace::destroy);
    }
}
