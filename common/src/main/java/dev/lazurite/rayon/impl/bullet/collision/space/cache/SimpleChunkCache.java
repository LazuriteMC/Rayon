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
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleChunkCache implements ChunkCache {
    private static final Hash.Strategy<BlockPos> QUICK_BLOCK_POS = new Hash.Strategy<>() {
        @Override
        public int hashCode(BlockPos pos) {
            return pos.hashCode();
        }

        @Override
        public boolean equals(BlockPos pos, BlockPos k1) {
            return k1 != null && (pos.getX() == k1.getX() && pos.getY() == k1.getY() && pos.getZ() == k1.getZ());
        }
    };

    private final MinecraftSpace space;
    private final Map<BlockPos, BlockData> blockData;
    private final List<FluidColumn> fluidColumns;
    private final Long2ObjectMap<List<FluidColumn>> fluidColumnByIndex;
    private final Set<BlockPos> activePositions;
    private final Long2ObjectMap<List<BlockPos>> activeColumn;

    SimpleChunkCache(MinecraftSpace space) {
        this.space = space;
        this.blockData = new ConcurrentHashMap<>();
        this.fluidColumns = new ArrayList<>();
        this.activePositions = new ObjectOpenCustomHashSet<>(QUICK_BLOCK_POS);
        this.activeColumn = new Long2ObjectOpenHashMap<>();
        this.fluidColumnByIndex = new Long2ObjectOpenHashMap<>();
    }

    @Override
    public void loadFluidData(BlockPos blockPos) {
        final var level = space.getLevel();

        if (!level.getFluidState(blockPos).isEmpty()) {
            var columns = this.fluidColumnByIndex.get(columnIndex(blockPos));

            if (columns == null || columns.stream().noneMatch(column -> column.contains(blockPos))) {
                var column = new FluidColumn(new BlockPos(blockPos), level);
                this.fluidColumns.add(column);
                this.fluidColumnByIndex.computeIfAbsent(column.getIndex(), (a) -> new ArrayList<>()).add(column);
            }
        }
    }

    @Override
    public void loadBlockData(BlockPos blockPos) {
        this.blockData.remove(blockPos);

        final var level = space.getLevel();
        final var blockState = level.getBlockState(blockPos);
        final var blockPos2 = blockPos.immutable();

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
        this.activeColumn.clear();

        for (var rigidBody : space.getRigidBodiesByClass(ElementRigidBody.class)) {
            if (!rigidBody.terrainLoadingEnabled() || !rigidBody.isActive()) {
                continue;
            }

            final var aabb = rigidBody.getCurrentMinecraftBoundingBox().inflate(1.0f);

            BlockPos.betweenClosedStream(aabb).forEach(blockPos -> {
                this.activeColumn.computeIfAbsent(columnIndex(blockPos), (a) -> new ArrayList<>()).add(blockPos.immutable());
                this.activePositions.add(blockPos.immutable());

                var blockData = this.blockData.get(blockPos);

                if (blockData != null) {
                    final var blockState = level.getBlockState(blockPos);

                    if (blockData.blockState() != blockState) {
                        loadBlockData(blockPos);
                    }
                } else {
                    loadBlockData(blockPos);
                }

                if (this.getFluidColumn(blockPos).isEmpty()) {
                    loadFluidData(blockPos);
                }
            });
        }

        this.blockData.keySet().removeIf(blockPos -> !this.activePositions.contains(blockPos));
        this.fluidColumns.removeIf(column -> {
            var x = !isInActiveColumn(column);

            if (x) {
                var y = this.fluidColumnByIndex.get(column.getIndex());
                if (y != null) {
                    y.remove(column);
                }
            }

            return x;
        });
    }

    private static long columnIndex(BlockPos blockPos) {
        return Integer.toUnsignedLong(blockPos.getX()) << 32l | Integer.toUnsignedLong(blockPos.getZ());
    }

    private boolean isInActiveColumn(FluidColumn column) {
        var list = this.activeColumn.get(column.getIndex());
        if (list == null) {
            return false;
        }

        for (var e : list) {
            if (column.contains(e)) {
                return true;
            }
        }
        return false;
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
        var allColumns = this.fluidColumnByIndex.get(columnIndex(blockPos));

        if (allColumns != null) {
            for (var column : allColumns) {
                if (column.contains(blockPos)) {
                    return Optional.of(column);
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public boolean isActive(BlockPos blockPos) {
        return this.activePositions.contains(blockPos);
    }
}