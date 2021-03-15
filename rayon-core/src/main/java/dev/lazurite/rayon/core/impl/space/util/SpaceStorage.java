package dev.lazurite.rayon.core.impl.space.util;

import dev.lazurite.rayon.core.impl.mixin.common.WorldMixin;
import dev.lazurite.rayon.core.impl.space.MinecraftSpace;
import net.minecraft.world.World;

/**
 * Used for storing a {@link MinecraftSpace} within any
 * {@link World} object.
 * @see WorldMixin
 */
public interface SpaceStorage {
    void setSpace(MinecraftSpace space);
    MinecraftSpace getSpace();
}
