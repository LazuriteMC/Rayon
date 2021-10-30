package dev.lazurite.rayon.core.impl.bullet.collision.space.storage;

import dev.lazurite.rayon.core.impl.mixin.common.LevelMixin;
import dev.lazurite.rayon.core.impl.bullet.collision.space.MinecraftSpace;
import net.minecraft.world.level.Level;

/**
 * Used for storing a {@link MinecraftSpace} within any
 * {@link Level} object.
 * @see LevelMixin
 */
public interface SpaceStorage {
    void setSpace(MinecraftSpace space);
    MinecraftSpace getSpace();
}