package dev.lazurite.rayon.core.impl.thread.space.util;

import dev.lazurite.rayon.core.impl.mixin.common.WorldMixin;
import dev.lazurite.rayon.core.impl.thread.space.MinecraftSpace;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.List;

/**
 * Used for storing a {@link MinecraftSpace} within any
 * {@link World} object.
 * @see WorldMixin
 */
public interface SpaceStorage {
    void putSpace(Identifier identifier, MinecraftSpace space);
    MinecraftSpace getSpace(Identifier identifier);
    List<MinecraftSpace> getSpaces();
}
