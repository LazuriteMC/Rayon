package dev.lazurite.rayon.impl.bullet.collision.space.cache.forge;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

public class ChunkCacheImpl {
    public static ResourceLocation getRegistryIdFor(Block block) {
        return ForgeRegistries.BLOCKS.getKey(block);
    }
}