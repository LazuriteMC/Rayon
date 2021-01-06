package dev.lazurite.rayon.mixin.common.world;

import dev.lazurite.rayon.physics.world.MinecraftDynamicsWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(World.class)
public class WorldMixin {
    @Inject(method = "close", at = @At("HEAD"))
    public void close(CallbackInfo info) throws IOException {
        MinecraftDynamicsWorld.get((World) (Object) this).destroy();
    }
}
