package dev.lazurite.rayon.particle.impl;

import dev.lazurite.rayon.core.impl.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.space.util.SpaceStorage;
import dev.lazurite.rayon.core.impl.util.event.BetterClientLifecycleEvents;
import net.fabricmc.api.ClientModInitializer;

public class RayonParticleClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BetterClientLifecycleEvents.LOAD_WORLD.register((client, world) ->
                ((SpaceStorage) world).putSpace(MinecraftSpace.MAIN, new MinecraftSpace(world)));
    }
}
