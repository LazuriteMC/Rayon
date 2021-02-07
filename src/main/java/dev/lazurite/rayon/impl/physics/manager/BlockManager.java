package dev.lazurite.rayon.impl.physics.manager;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jme3.bullet.collision.shapes.CollisionShape;
import dev.lazurite.rayon.impl.physics.body.EntityRigidBody;
import dev.lazurite.rayon.impl.physics.body.BlockRigidBody;
import dev.lazurite.rayon.impl.physics.body.shape.BoundingBoxShape;
import dev.lazurite.rayon.impl.physics.body.shape.PatternShape;
import dev.lazurite.rayon.impl.physics.body.type.BlockLoadingBody;
import dev.lazurite.rayon.impl.physics.world.MinecraftDynamicsWorld;
import dev.lazurite.rayon.impl.transporter.api.Disassembler;
import dev.lazurite.rayon.impl.transporter.api.buffer.BufferStorage;
import dev.lazurite.rayon.impl.transporter.api.buffer.PatternBuffer;
import dev.lazurite.rayon.impl.transporter.api.pattern.Pattern;
import dev.lazurite.rayon.impl.util.config.Config;
import net.minecraft.block.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * This class is used primarily by {@link MinecraftDynamicsWorld} in order
 * to load and unload blocks from the simulation. The reason not every block is loaded is
 * because it is too resource intensive to track thousands of blocks within the physics
 * simulation. Instead, only a set amount are available within the world at a time. This
 * value is controlled in the {@link Config} class as the blockDistance
 * integer value.
 * @see MinecraftDynamicsWorld
 * @see Config
 */
public class BlockManager {
    private final List<BlockRigidBody> toKeep = Lists.newArrayList();
    private final MinecraftDynamicsWorld dynamicsWorld;

    public BlockManager(MinecraftDynamicsWorld dynamicsWorld) {
        this.dynamicsWorld = dynamicsWorld;
    }

    /**
     * Load every block within a set distance from the given entities. The distance is defined
     * earlier during execution and converted into a {@link Box} area parameter.
     * @param blockLoadingBodies the {@link List} of {@link EntityRigidBody} objects
     * @see BlockManager#load(Box)
     */
    public void load(List<BlockLoadingBody> blockLoadingBodies) {
        int blockDistance = Config.getInstance().getLocal().getBlockDistance();

        blockLoadingBodies.forEach(body -> {
            if (!body.isNoClipEnabled()) {
                load(new Box(body.getBlockPos()).expand(blockDistance));
            }
        });

        purge();
    }

    /**
     * Loads an individual entity's block area into the physics simulation. This
     * is also where each block's {@link BlockRigidBody} object is instantiated
     * and properties such as position, shape, friction, etc. are applied here.
     * @param area the {@link Box} area around the entity to search for blocks within
     * @see BlockManager#load(List)
     */
    public void load(Box area) {
        World world = dynamicsWorld.getWorld();
        Map<BlockPos, BlockState> blockList = getBlockList(world, area);

        blockList.forEach((blockPos, blockState) -> {
            float friction = 1.5f;

            /* Will be replaced with json code in a future version */
            if (blockState.getBlock() instanceof IceBlock) {
                friction = 0.05F;
            } else if (!(blockState.getBlock() instanceof HoneyBlock) && !(blockState.getBlock() instanceof SlimeBlock) && !(blockState.getBlock() instanceof SoulSandBlock)) {
                friction = 0.9F;
            }

            /* Check if the block is solid or not */
            if (!blockState.getBlock().canMobSpawnInside()) {
                BlockRigidBody body;

                if (!blockState.isFullCube(world, blockPos)) {
                    if (world.isClient()) {
                        MatrixStack transformation = new MatrixStack();
                        transformation.translate(-0.5f, -0.5f, -0.5f);
                        body = load(blockPos, blockState, world, friction, 0.25f, Disassembler.getBlock(blockState, blockPos, world, transformation));
                    } else {
                        body = load(blockPos, blockState, world, friction, 0.25f, PatternBuffer.getBlockBuffer(world).pop(blockPos));
                    }
                } else {
                    body = load(blockPos, blockState, world, friction, 0.25f, null);
                }

                toKeep.add(body);
            }
        });
    }

    public BlockRigidBody load(BlockPos blockPos, BlockState blockState, World world, float friction, float restitution, @Nullable Pattern pattern) {
        CollisionShape shape = null;

        if (pattern != null) {
            shape = new PatternShape(pattern);
        } else {
            VoxelShape voxel = blockState.getCollisionShape(world, blockPos);

            if (!voxel.isEmpty()) {
                shape = new BoundingBoxShape(voxel.getBoundingBox());
            }
        }

        BlockRigidBody body = findBlockAtPos(blockPos);

        if (body == null) {
            body = new BlockRigidBody(blockState, blockPos, shape);
        }

        body.setFriction(friction);
        body.setRestitution(restitution);

        if (!dynamicsWorld.getRigidBodyList().contains(body)) {
            dynamicsWorld.addCollisionObject(body);
        }

        return body;
    }

    /**
     * Prune out any unnecessary blocks from the world during each call
     * to {@link MinecraftDynamicsWorld#step}. The purpose is to prevent
     * any trailing or residual blocks from being left over from a
     * previous {@link BlockManager#load(List)} call.
     * <b>Note:</b> This method should only be called after every entity
     * has been passed through the loading process. Otherwise, blocks will
     * be removed from the simulation prematurely and cause you a headache.
     * @see BlockManager#load(List)
     */
    public void purge() {
        List<BlockRigidBody> toRemove = Lists.newArrayList();

        dynamicsWorld.getRigidBodiesByClass(BlockRigidBody.class).forEach(body -> {
            if (!toKeep.contains(body)) {
                toRemove.add(body);
            }
        });

        toRemove.forEach(dynamicsWorld::removeCollisionObject);
        toKeep.clear();
    }

    public BlockRigidBody findBlockAtPos(BlockPos blockPos) {
        for (BlockRigidBody body : dynamicsWorld.getRigidBodiesByClass(BlockRigidBody.class)) {
            if (body.getBlockPos().equals(blockPos)) {
                return body;
            }
        }

        return null;
    }

    /**
     * Simply returns a basic {@link Map} of {@link BlockPos} and {@link BlockState}
     * objects representing the blocks that make up the {@link Box} area parameter.
     * @param world the {@link World} to retrieve block info from
     * @param area  the {@link Box} area within the world to retrieve block info from
     * @return the {@link Map} of {@link BlockPos} and {@link BlockState} objects
     * @see BlockManager#load(Box)
     */
    public static Map<BlockPos, BlockState> getBlockList(World world, Box area) {
        Map<BlockPos, BlockState> map = Maps.newHashMap();

        for (int i = (int) area.minX; i < area.maxX; i++) {
            for (int j = (int) area.minY; j < area.maxY; j++) {
                for (int k = (int) area.minZ; k < area.maxZ; k++) {
                    BlockPos blockPos = new BlockPos(i, j, k);
                    BlockView chunk = world.getChunkManager().getChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4);

                    if (chunk != null) {
                        map.put(blockPos, chunk.getBlockState(blockPos));
                    }
                }
            }
        }

        return map;
    }
}
