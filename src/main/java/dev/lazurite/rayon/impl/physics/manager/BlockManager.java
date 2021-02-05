package dev.lazurite.rayon.impl.physics.manager;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import dev.lazurite.rayon.impl.physics.body.EntityRigidBody;
import dev.lazurite.rayon.impl.physics.body.BlockRigidBody;
import dev.lazurite.rayon.impl.physics.body.shape.PatternShape;
import dev.lazurite.rayon.impl.physics.body.type.BlockLoadingBody;
import dev.lazurite.rayon.impl.physics.world.MinecraftDynamicsWorld;
import dev.lazurite.rayon.impl.transporter.api.Disassembler;
import dev.lazurite.rayon.impl.transporter.api.pattern.Pattern;
import dev.lazurite.rayon.impl.transporter.api.buffer.PatternBuffer;
import dev.lazurite.rayon.impl.transporter.api.pattern.TypedPattern;
import dev.lazurite.rayon.impl.util.config.Config;
import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.registry.Registry;
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
                BlockRigidBody body = findBlockAtPos(blockPos, blockState);

                /* Try to make a new rigid body if it's state or shape is different */
                if (hasBlockChanged(body)) {
                    if (body != null) dynamicsWorld.removeCollisionObject(body);

                    body = new BlockRigidBody(blockState, blockPos, world, friction, 0.25f);

                    /* Check if the block isn't already in the dynamics world */
                    if (!dynamicsWorld.getRigidBodyList().contains(body)) {
                        dynamicsWorld.addCollisionObject(body);
                    }
                }

                toKeep.add(body);
            }
        });
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

    public boolean hasBlockChanged(@Nullable BlockRigidBody body) {
        if (body == null) return true;

        BlockState blockState = body.getBlockState();
        BlockPos blockPos = body.getBlockPos();
        World world = dynamicsWorld.getWorld();

//        if (!blockState.equals(world.getBlockState(blockPos))) {
//            return true;
//        }

        if (body.getCollisionShape() instanceof PatternShape) {
            if (world.isClient()) {
                return !Disassembler.getBlock(blockState, blockPos, world).equals(((PatternShape) body.getCollisionShape()).getPattern());
            } else {
                return PatternBuffer.getBlockBuffer(world).get(blockPos).contains(((PatternShape) body.getCollisionShape()).getPattern());
            }
        }

         return false;
    }

    public BlockRigidBody findBlockAtPos(BlockPos blockPos, BlockState blockState) {
        for (BlockRigidBody body : dynamicsWorld.getRigidBodiesByClass(BlockRigidBody.class)) {
            if (body.getBlockPos().equals(blockPos) && body.getBlockState().equals(blockState)) {
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
