package dev.lazurite.rayon.mixin.common;

import dev.lazurite.rayon.physics.world.MinecraftDynamicsWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(World.class)
public class WorldMixin {
    @Inject(at = @At("HEAD"), method = "close()V")
    public void close(CallbackInfo info) {
        MinecraftDynamicsWorld.get((World) (Object) this).destroy();
    }
}
