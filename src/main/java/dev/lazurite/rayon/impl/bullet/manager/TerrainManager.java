package dev.lazurite.rayon.impl.bullet.manager;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jme3.bullet.collision.shapes.CollisionShape;
import dev.lazurite.rayon.impl.bullet.body.BlockRigidBody;
import dev.lazurite.rayon.impl.bullet.body.shape.BoundingBoxShape;
import dev.lazurite.rayon.impl.bullet.body.shape.PatternShape;
import dev.lazurite.rayon.impl.bullet.body.type.TerrainLoadingBody;
import dev.lazurite.rayon.impl.bullet.world.MinecraftSpace;
import dev.lazurite.rayon.impl.util.config.Config;
import dev.lazurite.transporter.api.Disassembler;
import dev.lazurite.transporter.api.buffer.PatternBuffer;
import dev.lazurite.transporter.api.pattern.TypedPattern;
import net.minecraft.block.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.*;

/**
 * This class is used primarily by {@link MinecraftSpace} in order
 * to load and unload blocks from the simulation. The reason not every block is loaded is
 * because it is too resource intensive to track thousands of blocks within the physics
 * simulation. Instead, only a set amount are available within the world at a time. This
 * value is controlled in the {@link Config} class as the blockDistance
 * integer value.
 * @see MinecraftSpace
 * @see Config
 */
public class TerrainManager {
    private final List<BlockRigidBody> toKeep = Lists.newArrayList();
    private final MinecraftSpace dynamicsWorld;

    public TerrainManager(MinecraftSpace dynamicsWorld) {
        this.dynamicsWorld = dynamicsWorld;
    }

    /**
     * Load every block within a set distance from the given entities. The distance is defined
     * earlier during execution and converted into a {@link Box} area parameter.
     * @param blockLoadingBodies the {@link List} of {@link TerrainLoadingBody} objects
     * @see TerrainManager#load(Box)
     */
    public void load(List<TerrainLoadingBody> blockLoadingBodies) {
        int blockDistance = Config.getInstance().getLocal().getBlockDistance();

        blockLoadingBodies.forEach(body -> {
            load(new Box(body.getBlockPos()).expand(blockDistance));
        });

        purge();
    }

    /**
     * Loads an individual entity's block area into the physics simulation. This
     * is also where each block's {@link BlockRigidBody} object is instantiated
     * and properties such as position, shape, friction, etc. are applied here.
     * @param area the {@link Box} area around the entity to search for blocks within
     * @see TerrainManager#load(List)
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
                BlockRigidBody body = findBlockAtPos(blockPos);

                /* Make a new rigid body if there isn't already one */
                if (body == null) {
                    VoxelShape voxel = blockState.getCollisionShape(world, blockPos);
                    CollisionShape shape;

                    if (!voxel.isEmpty()) {
                        shape = new BoundingBoxShape(voxel.getBoundingBox());
                    } else {
                        shape = new BoundingBoxShape(new Box(-0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f));
                    }

                    body = new BlockRigidBody(blockState, blockPos, shape, friction, 0.25f);
                }

                /* Make a pattern shape if applicable */
                if (!blockState.isFullCube(world, blockPos)) {
                    TypedPattern<BlockPos> pattern;

                    if (world.isClient()) {
                        MatrixStack transformation = new MatrixStack();
                        transformation.scale(0.95f, 0.95f, 0.95f);
                        transformation.translate(-0.5f, -0.5f, -0.5f);
                        pattern = Disassembler.getBlock(blockState, blockPos, world, transformation);
                    } else {
                        pattern = PatternBuffer.getBlockBuffer(world).get(blockPos);
                    }

                    if (pattern != null) {
                        if (body.getCollisionShape() instanceof PatternShape) {
                            if (!pattern.equals(((PatternShape) body.getCollisionShape()).getPattern())) {
                                body.setCollisionShape(new PatternShape(pattern));

                                if (world.isClient()) {
                                    PatternBuffer.getBlockBuffer(world).put(pattern);
                                }
                            }
                        } else {
                            body.setCollisionShape(new PatternShape(pattern));

                            if (world.isClient()) {
                                PatternBuffer.getBlockBuffer(world).put(pattern);
                            }
                        }
                    }
                } else if (body.getCollisionShape() instanceof PatternShape) {
                    VoxelShape voxel = blockState.getCollisionShape(world, blockPos);

                    if (!voxel.isEmpty()) {
                        body.setCollisionShape(new BoundingBoxShape(voxel.getBoundingBox()));
                    } else {
                        body.setCollisionShape(new BoundingBoxShape(new Box(-0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f)));
                    }
                }

                if (!dynamicsWorld.getRigidBodyList().contains(body)) {
                    dynamicsWorld.addCollisionObject(body);
                }

                toKeep.add(body);
            }
        });
    }

    /**
     * Prune out any unnecessary blocks from the world during each call
     * to {@link MinecraftSpace#step}. The purpose is to prevent
     * any trailing or residual blocks from being left over from a
     * previous {@link TerrainManager#load(List)} call.
     * <b>Note:</b> This method should only be called after every entity
     * has been passed through the loading process. Otherwise, blocks will
     * be removed from the simulation prematurely and cause you a headache.
     * @see TerrainManager#load(List)
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
     * @see TerrainManager#load(Box)
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
