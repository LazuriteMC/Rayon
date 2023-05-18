package dev.lazurite.rayon.impl.bullet.collision.space.cache;

import com.jme3.bounding.BoundingBox;
import dev.lazurite.rayon.impl.Rayon;
import dev.lazurite.rayon.impl.bullet.collision.body.ElementRigidBody;
import dev.lazurite.rayon.impl.bullet.collision.space.MinecraftSpace;
import dev.lazurite.rayon.impl.bullet.collision.space.block.BlockProperty;
import dev.lazurite.transporter.api.pattern.Pattern;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

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
    private final LongSet activePositions;
    private final Long2ObjectMap<List<BlockPos>> activeColumn;

    SimpleChunkCache(MinecraftSpace space) {
        this.space = space;
        this.blockData = new ConcurrentHashMap<>();
        this.fluidColumns = new ArrayList<>();
        this.activePositions = new LongOpenHashSet();
        this.activeColumn = new Long2ObjectOpenHashMap<>(65536);
        this.fluidColumnByIndex = new Long2ObjectOpenHashMap<>(65536);
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
        final var level = space.getLevel();
        final var blockState = level.getBlockState(blockPos);

        loadBlockData(blockPos.immutable(), level, blockState);
    }

    private void loadBlockData(BlockPos blockPos, Level level, BlockState blockState) {
        if (ChunkCache.isValidBlock(blockState)) {
            this.blockData.put(blockPos, new BlockData(level, blockPos, blockState, ShapeCache.getShapeFor(blockState, level, blockPos)));
        } else {
            this.blockData.remove(blockPos);
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

            final var aabb = rigidBody.getCurrentMinecraftBoundingBox().inflate(1.0f + Mth.sqrt(rigidBody.getSquaredSpeed()) / 20);

            BlockPos.betweenClosedStream(aabb).forEach(blockPos -> {
                if (this.activePositions.contains(blockPos.asLong())) {
                    return;
                }

                var pos = blockPos.immutable();
                this.activeColumn.computeIfAbsent(columnIndex(pos), (a) -> new ObjectArrayList<>(512)).add(pos);
                this.activePositions.add(pos.asLong());

                var blockData = this.blockData.get(pos);
                final var blockState = level.getBlockState(pos);

                if (blockData != null) {
                    if (blockData.blockState() != blockState) {
                        loadBlockData(pos, level, blockState);
                    }
                } else {
                    loadBlockData(pos, level, blockState);
                }

                if (this.getFluidColumn(pos).isEmpty()) {
                    loadFluidData(pos);
                }
            });
        }

        this.blockData.keySet().removeIf(blockPos -> !this.activePositions.contains(blockPos.asLong()));
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
        return this.activePositions.contains(blockPos.asLong());
    }
}