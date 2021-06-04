package dev.lazurite.rayon.core.impl.physics.space.environment;

import dev.lazurite.rayon.core.impl.physics.space.MinecraftSpace;

public interface WorldComponent {
    void apply(MinecraftSpace space);
}