package dev.lazurite.rayon.particle.impl.space;

import dev.lazurite.rayon.core.impl.physics.PhysicsThread;
import dev.lazurite.rayon.core.impl.physics.space.MinecraftSpace;
import dev.lazurite.rayon.particle.impl.RayonParticleClient;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class ParticleSpace extends MinecraftSpace {
    public static final Identifier PARTICLE = new Identifier(RayonParticleClient.MODID, "particle");

    public ParticleSpace(PhysicsThread thread, World world) {
        super(thread, world);
    }

    @Override
    public void step(float delta) {

        super.step(delta);
    }
}
