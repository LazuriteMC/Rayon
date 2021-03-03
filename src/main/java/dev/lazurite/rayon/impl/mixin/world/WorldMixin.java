package dev.lazurite.rayon.impl.mixin.world;

import dev.lazurite.rayon.impl.Rayon;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * This mixin handles the joining of world
 * physics threads during the closing phase.
 */
@Mixin(World.class)
public abstract class WorldMixin {
    @Inject(method = "close", at = @At("HEAD"))
    public void close(CallbackInfo info) {
        Rayon.THREAD.get((World) (Object) this).destroy();
    }
}
