package dev.lazurite.rayon.impl.util.space;

import dev.lazurite.rayon.impl.bullet.space.MinecraftSpace;
import dev.lazurite.rayon.impl.mixin.common.WorldMixin;
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
