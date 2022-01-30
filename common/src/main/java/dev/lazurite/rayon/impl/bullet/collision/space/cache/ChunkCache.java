package dev.lazurite.rayon.impl.bullet.collision.space.cache;

import com.jme3.math.Vector3f;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.lazurite.rayon.impl.bullet.collision.body.shape.MinecraftShape;
import dev.lazurite.rayon.impl.bullet.collision.space.MinecraftSpace;
import dev.lazurite.rayon.impl.bullet.collision.space.block.BlockProperty;
import dev.lazurite.rayon.impl.bullet.math.Convert;
import dev.lazurite.transporter.api.Disassembler;
import dev.lazurite.transporter.api.pattern.Pattern;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;

import java.util.*;

/**
 * Used for storing block that can be queried during physics execution.
 * An implementation of this should be updated/reloaded every tick on the
 * main game thread.
 * @see MinecraftSpace#step
 */
public interface ChunkCache {
    static ChunkCache create(MinecraftSpace space) {
        return new SimpleChunkCache(space);
    }

    static boolean isValidBlock(BlockState blockState) {
        if (blockState == null) {
            return false;
        }

        final var block = blockState.getBlock();
        final var properties = BlockProperty.getBlockProperty(block);

        return properties != null ? properties.collidable() :
                !blockState.isAir() &&
                !block.isPossibleToRespawnInThis() && (
                        blockState.getFluidState().isEmpty() || (
                                blockState.hasProperty(BlockStateProperties.WATERLOGGED) &&
                                blockState.getValue(BlockStateProperties.WATERLOGGED)
                        )
                );
    }

    static Pattern genShapeForBlock(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState) {
        final var blockEntity = blockGetter.getBlockEntity(blockPos);
        final var transformation = new PoseStack();
        transformation.scale(0.95f, 0.95f, 0.95f);
        transformation.translate(-0.5f, -0.5f, -0.5f);

        try {
            if (blockEntity != null) {
                return Disassembler.getBlockEntity(blockEntity, transformation);
            } else {
                return Disassembler.getBlock(blockState, transformation);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    void refreshAll();
    void loadBlockData(BlockPos blockPos);
    void loadFluidData(BlockPos blockPos);

    MinecraftSpace getSpace();
    List<BlockData> getBlockData();
    List<FluidColumn> getFluidColumns();
    Optional<BlockData> getBlockData(BlockPos blockPos);
    Optional<FluidColumn> getFluidColumn(BlockPos blockPos);

    record BlockData (Level level, BlockPos blockPos, BlockState blockState, MinecraftShape shape) { }
    record FluidData (Level level, BlockPos blockPos, FluidState fluidState) { }

    class FluidColumn {
        private final FluidData top;
        private final FluidData bottom;
        private final Vector3f flow;
        private final float topHeight;

        public FluidColumn(BlockPos start, Level level) {
            final var cursor = new BlockPos(start).mutable();
            var fluidState = level.getFluidState(cursor);

            // find bottom block
            while (!fluidState.isEmpty()) {
                cursor.set(cursor.below());
                fluidState = level.getFluidState(cursor);
            }

            cursor.set(cursor.above()); // the above loop ends at one below the bottom
            fluidState = level.getFluidState(cursor);
            this.bottom = new FluidData(level, new BlockPos(cursor), level.getFluidState(cursor));

            // find top block
            while (!fluidState.isEmpty()) {
                cursor.set(cursor.above());
                fluidState = level.getFluidState(cursor);
            }

            // Water height (in case of flowing water)
            this.top = new FluidData(level, new BlockPos(cursor), level.getFluidState(cursor.below()));
            final var voxelShape = top.fluidState().getShape(level, top.blockPos().below());
            this.topHeight = voxelShape.isEmpty() ? 0.875f : (float) voxelShape.bounds().getYsize();

            // Water flow direction
            this.flow = Convert.toBullet(top.fluidState().getFlow(level, cursor.below()));
        }

        public boolean contains(BlockPos blockPos) {
            return top.blockPos.getX() == blockPos.getX()
                    && top.blockPos.getZ() == blockPos.getZ()
                    && top.blockPos.getY() >= blockPos.getY()
                    && bottom.blockPos.getY() <= blockPos.getY();
        }

        public FluidData getTop() {
            return this.top;
        }

        public FluidData getBottom() {
            return this.bottom;
        }

        public float getTopHeight(Vector3f pos) {
            if (pos.lengthSquared() > 1.0f) {
                throw new RuntimeException("Top height coordinates should be local (<= 1.0)");
            }

            return this.topHeight;
        }

        public int getHeight() {
            return this.top.blockPos.getY() - this.bottom.blockPos.getY();
        }

        public Vector3f getFlow() {
            return this.flow;
        }
    }
}