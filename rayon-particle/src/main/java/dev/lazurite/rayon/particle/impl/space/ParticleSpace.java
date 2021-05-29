package dev.lazurite.rayon.particle.impl.space;

import dev.lazurite.rayon.core.impl.physics.PhysicsThread;
import dev.lazurite.rayon.core.impl.physics.space.MinecraftSpace;
import dev.lazurite.rayon.particle.impl.RayonParticle;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.function.BooleanSupplier;

public class ParticleSpace extends MinecraftSpace {
    public static final Identifier PARTICLE = new Identifier(RayonParticle.MODID, "particle");

    public ParticleSpace(PhysicsThread thread, World world) {
        super(thread, world);
    }

    @Override
    public void step(BooleanSupplier shouldStep) {
        super.step(shouldStep);
    }
}
