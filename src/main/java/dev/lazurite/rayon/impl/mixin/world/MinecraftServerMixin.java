package dev.lazurite.rayon.impl.mixin.world;

import dev.lazurite.rayon.impl.Rayon;
import dev.lazurite.rayon.impl.bullet.world.MinecraftSpace;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Shadow @Final private Map<RegistryKey<World>, ServerWorld> worlds;

    @Inject(method = "stop", at = @At("HEAD"))
    public void stop(boolean bl, CallbackInfo info) {
        System.out.println("KILLING PHYSICS");
        worlds.values().forEach(world ->
            Rayon.THREAD.get(world).execute(MinecraftSpace::destroy)
        );
    }
}
