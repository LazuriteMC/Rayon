package dev.lazurite.rayon.impl.bullet.collision.space.cache.fabric;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public class ChunkCacheImpl {
    public static ResourceLocation getRegistryIdFor(Block block) {
        return Registry.BLOCK.getKey(block);
    }
}