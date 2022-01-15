package dev.lazurite.rayon.impl.bullet.collision.space.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public record BlockProperty (float friction, float restitution, boolean collidable, boolean isFullBlock) {
    private static final Map<Block, BlockProperty> blockProperties = new ConcurrentHashMap<>();

    static {
        BlockProperty.addBlockProperty(Blocks.ICE, 0.05f, 0.25f, true, true);
        BlockProperty.addBlockProperty(Blocks.SLIME_BLOCK, 3.0f, 3.0f, true, true);
        BlockProperty.addBlockProperty(Blocks.HONEY_BLOCK, 3.0f, 0.25f, true, true);
        BlockProperty.addBlockProperty(Blocks.SOUL_SAND, 3.0f, 0.25f, true, true);
        BlockProperty.addBlockProperty(Blocks.LECTERN, 0.75f, 0.25f, true, false);
        BlockProperty.addBlockProperty(Blocks.SNOW, 1.0f, 0.15f, true, true);
    }

    public static void addBlockProperty(Block block, float friction, float restitution, boolean collidable, boolean isFullBlock) {
        blockProperties.put(block, new BlockProperty(Math.max(friction, 0.0f), Math.max(restitution, 0.0f), collidable, isFullBlock));
    }

    public static BlockProperty getBlockProperty(Block block) {
        return blockProperties.get(block);
    }
}