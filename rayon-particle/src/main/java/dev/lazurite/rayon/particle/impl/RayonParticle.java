package dev.lazurite.rayon.particle.impl;

import dev.lazurite.rayon.core.api.event.PhysicsSpaceEvents;
import dev.lazurite.rayon.core.impl.physics.space.util.SpaceStorage;
import dev.lazurite.rayon.particle.impl.space.ParticleSpace;
import net.fabricmc.api.ClientModInitializer;

public class RayonParticle implements ClientModInitializer {
    public static final String MODID = "rayon-particle";

    @Override
    public void onInitializeClient() {
        PhysicsSpaceEvents.PREINIT.register((thread, world) ->
                ((SpaceStorage) world).putSpace(ParticleSpace.PARTICLE, new ParticleSpace(thread, world)));
    }
}
