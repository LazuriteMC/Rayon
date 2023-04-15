package dev.lazurite.rayon.impl.bullet.collision.space.cache;

import dev.lazurite.rayon.impl.bullet.collision.body.shape.MinecraftShape;
import dev.lazurite.rayon.impl.bullet.collision.space.block.BlockProperty;
import dev.lazurite.transporter.api.pattern.Pattern;
import dev.lazurite.transporter.impl.Transporter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.IdentityHashMap;

public final class ShapeCache {
    private static final IdentityHashMap<BlockState, MinecraftShape> SHAPES = new IdentityHashMap<>();

    public static MinecraftShape getShapeFor(BlockState blockState, Level level, BlockPos blockPos) {
        if (blockState.getBlock().hasDynamicShape()) {
            return createShapeFor(blockState, level, blockPos);
        }

        var shape = SHAPES.get(blockState);

        if (shape == null) {
            shape = createShapeFor(blockState, level, BlockPos.ZERO);
            SHAPES.put(blockState, shape);
        }

        return shape;
    }

    private static MinecraftShape createShapeFor(BlockState blockState, Level level, BlockPos blockPos) {
        final var properties = BlockProperty.getBlockProperty(blockState.getBlock());
        MinecraftShape shape = null;

        if (!blockState.isCollisionShapeFullBlock(level, blockPos) || (properties != null && !properties.isFullBlock())) {
            Pattern pattern;

            if (level.isClientSide) {
                pattern = ChunkCache.genShapeForBlock(level, blockPos, blockState);
            } else {
                pattern = Transporter.getPatternBuffer().getBlock(Block.getId(blockState));
            }

            if (pattern != null && !pattern.getQuads().isEmpty()) {
                shape = MinecraftShape.concave(pattern);
            }
        }

        if (shape == null) {
            final var voxelShape = blockState.getCollisionShape(level, blockPos);
            final var boundingBox = voxelShape.isEmpty() ? new AABB(-0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f) : voxelShape.bounds();
            shape = MinecraftShape.convex(boundingBox);
        }
        return shape;
    }
}
