package dev.lazurite.rayon.core.impl.bullet.space.components;

import dev.lazurite.rayon.core.impl.bullet.space.MinecraftSpace;

public interface WorldComponent {
    void apply(MinecraftSpace space);
}