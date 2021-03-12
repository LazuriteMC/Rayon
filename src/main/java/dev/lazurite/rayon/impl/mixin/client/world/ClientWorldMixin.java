package dev.lazurite.rayon.impl.mixin.client.world;

import dev.lazurite.rayon.impl.bullet.space.MinecraftSpace;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Destroy all {@link MinecraftSpace}s when the
 * client disconnects from the server.
 */
@Mixin(ClientWorld.class)
public class ClientWorldMixin {
    @Inject(method = "disconnect", at = @At("HEAD"))
    public void disconnect(CallbackInfo info) {
        MinecraftSpace.get((World) (Object) this).getThread().clearSpaces();
        System.out.println("CLEAR");
    }
}
