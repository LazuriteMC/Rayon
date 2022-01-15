package dev.lazurite.rayon.impl.bullet.collision.space.cache;

import com.jme3.bounding.BoundingBox;
import dev.lazurite.rayon.impl.Rayon;
import dev.lazurite.rayon.impl.bullet.collision.body.ElementRigidBody;
import dev.lazurite.rayon.impl.bullet.collision.body.shape.MinecraftShape;
import dev.lazurite.rayon.impl.bullet.collision.space.MinecraftSpace;
import dev.lazurite.rayon.impl.bullet.collision.space.block.BlockProperty;
import dev.lazurite.rayon.impl.bullet.math.Convert;
import dev.lazurite.transporter.api.pattern.Pattern;
import dev.lazurite.transporter.impl.Transporter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleChunkCache implements ChunkCache {
    private final MinecraftSpace space;
    private final Map<BlockPos, BlockData> blockData;
    private final List<FluidColumn> fluidColumns;
    private final List<BlockPos> activePositions;

    SimpleChunkCache(MinecraftSpace space) {
        this.space = space;
        this.blockData = new ConcurrentHashMap<>();
        this.fluidColumns = new ArrayList<>();
        this.activePositions = new ArrayList<>();
    }

    @Override
    public void loadFluidData(BlockPos blockPos) {
        final var level = space.getLevel();

        if (!level.getFluidState(blockPos).isEmpty()) {
            if (this.fluidColumns.stream().noneMatch(column -> column.contains(blockPos))) {
                this.fluidColumns.add(new FluidColumn(new BlockPos(blockPos), level));
            }
        }
    }

    @Override
    public void loadBlockData(BlockPos blockPos) {
        this.blockData.remove(blockPos);

        final var level = space.getLevel();
        final var blockState = level.getBlockState(blockPos);
        final var blockPos2 = new BlockPos(blockPos);

        if (ChunkCache.isValidBlock(blockState)) {
            final var properties = BlockProperty.getBlockProperty(blockState.getBlock());
            MinecraftShape shape = null;

            if (!blockState.isCollisionShapeFullBlock(level, blockPos) || (properties != null && !properties.isFullBlock())) {
                Pattern pattern;

                if (space.isServer()) {
                    pattern = Transporter.getPatternBuffer().getBlock(Block.getId(blockState));
                } else {
                    pattern = ChunkCache.genShapeForBlock(level, blockPos, blockState);
                }

                if (pattern != null && !pattern.getQuads().isEmpty()) {
                    shape = MinecraftShape.concave(pattern);
                }
            }

            if (shape == null) {
                final var voxelShape = blockState.getCollisionShape(space.getLevel(), blockPos);
                final var boundingBox = voxelShape.isEmpty() ? new AABB(-0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f) : voxelShape.bounds();
                shape = MinecraftShape.convex(boundingBox);
            }

            this.blockData.put(blockPos2, new BlockData(level, blockPos2, blockState, shape));
        }
    }

    @Override
    public void refreshAll() {
        final var level = space.getLevel();
        this.activePositions.clear();

        for (var rigidBody : space.getRigidBodiesByClass(ElementRigidBody.class)) {
            if (!rigidBody.terrainLoadingEnabled() || !rigidBody.isActive()) {
                continue;
            }

            final var aabb = Convert.toMinecraft(rigidBody.boundingBox(new BoundingBox())).inflate(1.0f);

            BlockPos.betweenClosedStream(aabb).forEach(blockPos -> {
                this.activePositions.add(new BlockPos(blockPos));

                this.getBlockData(blockPos).ifPresentOrElse(blockData -> {
                    final var blockState = level.getBlockState(blockPos);

                    if (Block.getId(blockData.blockState()) != Block.getId(blockState)) {
                        loadBlockData(blockPos);
                    }
                }, () -> loadBlockData(blockPos));

                if (this.getFluidColumn(blockPos).isEmpty()) {
                    loadFluidData(blockPos);
                }
            });
        }

        this.blockData.keySet().removeIf(blockPos -> !this.activePositions.contains(blockPos));
        this.fluidColumns.removeIf(column -> this.activePositions.stream().noneMatch(column::contains));
    }

    @Override
    public MinecraftSpace getSpace() {
        return this.space;
    }

    @Override
    public List<BlockData> getBlockData() {
        return new ArrayList<>(this.blockData.values());
    }

    @Override
    public List<FluidColumn> getFluidColumns() {
        return new ArrayList<>(this.fluidColumns);
    }

    @Override
    public Optional<BlockData> getBlockData(BlockPos blockPos) {
        return Optional.ofNullable(this.blockData.get(blockPos));
    }

    @Override
    public Optional<FluidColumn> getFluidColumn(BlockPos blockPos) {
        for (var column : getFluidColumns()) {
            if (column.contains(blockPos)) {
                return Optional.of(column);
            }
        }

        return Optional.empty();
    }
}