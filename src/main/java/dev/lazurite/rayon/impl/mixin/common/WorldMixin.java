package dev.lazurite.rayon.impl.mixin.common;

import dev.lazurite.rayon.Rayon;
import dev.lazurite.rayon.impl.physics.world.MinecraftDynamicsWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * This mixin handles the joining of world physics threads
 * during the closing phase.
 * @see MinecraftDynamicsWorld
 */
@Mixin(World.class)
public class WorldMixin {
    @Inject(method = "close", at = @At("HEAD"))
    public void close(CallbackInfo info) {
        Rayon.WORLD.get((World) (Object) this).destroy();
    }
}
