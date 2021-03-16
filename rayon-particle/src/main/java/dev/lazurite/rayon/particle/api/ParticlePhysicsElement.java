package dev.lazurite.rayon.particle.api;

import dev.lazurite.rayon.core.api.PhysicsElement;

public interface ParticlePhysicsElement extends PhysicsElement {
    @Override
    default void reset() {

    }
}
