package dev.lazurite.rayon.impl.bullet.collision.space.cache;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.impl.bullet.collision.body.ElementRigidBody;
import dev.lazurite.rayon.impl.bullet.collision.body.shape.MinecraftShape;
import dev.lazurite.rayon.impl.bullet.collision.space.MinecraftSpace;
import dev.lazurite.rayon.impl.bullet.math.Convert;
import dev.lazurite.transporter.api.pattern.Pattern;
import dev.lazurite.transporter.impl.Transporter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleChunkCache implements ChunkCache {
    private final MinecraftSpace space;
    private final Map<BlockPos, BlockData> blockData;

    SimpleChunkCache(MinecraftSpace space) {
        this.space = space;
        this.blockData = new ConcurrentHashMap<>();
    }

    @Override
    public void loadBlockData(BlockPos blockPos) {
        this.blockData.remove(blockPos);

        final var level = space.getLevel();
        final var blockState = level.getBlockState(blockPos);
        if (!ChunkCache.isValidBlock(blockState)) return;

        MinecraftShape shape = null;

        if (!blockState.isCollisionShapeFullBlock(level, blockPos)) {
            Pattern pattern;

            if (space.isServer()) {
                pattern = Transporter.getPatternBuffer().get(ChunkCache.getRegistryIdFor(blockState.getBlock()));
            } else {
                pattern = ChunkCache.genShapeForBlock(level, blockPos, blockState);
            }

            if (pattern != null && !pattern.getQuads().isEmpty()) {
                shape = MinecraftShape.of(pattern);
            }
        }

        if (shape == null) {
            final var voxelShape = blockState.getCollisionShape(space.getLevel(), blockPos);
            final var boundingBox = voxelShape.isEmpty() ? new AABB(-0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f) : voxelShape.bounds();
            shape = MinecraftShape.of(boundingBox.deflate(0.05f));
        }

        final var blockPos2 = new BlockPos(blockPos);
        this.blockData.put(blockPos2, new BlockData(blockPos2, blockState, shape));
    }

    @Override
    public void refreshAll() {
        final var level = space.getLevel();

        for (var rigidBody : space.getRigidBodiesByClass(ElementRigidBody.class)) {
            if (!rigidBody.terrainLoadingEnabled() || !rigidBody.isActive()) {
                continue;
            }

            final var aabb = Convert.toMinecraft(rigidBody.boundingBox(new BoundingBox())).inflate(1.5f);

            BlockPos.betweenClosedStream(aabb).forEach(blockPos -> {
                if (this.blockData.containsKey(blockPos)) {
                    final var blockData = this.blockData.get(blockPos);
                    final var blockState = level.getBlockState(blockPos);

                    if (!blockData.blockState().equals(blockState)) {
                        loadBlockData(blockPos);
                    }
                } else {
                    loadBlockData(blockPos);
                }
            });
        }
    }

    @Override
    public Optional<BlockData> getBlockData(BlockPos blockPos) {
        return Optional.ofNullable(this.blockData.get(blockPos));
    }

    @Override
    public Optional<BlockData> getBlockData(Vector3f position) {
        return this.getBlockData(new BlockPos(position.x, position.y, position.z));
    }
}