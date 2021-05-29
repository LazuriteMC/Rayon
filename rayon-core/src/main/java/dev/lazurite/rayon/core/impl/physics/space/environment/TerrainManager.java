package dev.lazurite.rayon.core.impl.physics.space.environment;

import com.google.common.collect.Lists;
import com.jme3.bullet.collision.shapes.CollisionShape;
import dev.lazurite.rayon.core.impl.RayonCore;
import dev.lazurite.rayon.core.impl.physics.space.body.BlockRigidBody;
import dev.lazurite.rayon.core.impl.physics.space.body.MinecraftRigidBody;
import dev.lazurite.rayon.core.impl.physics.space.body.shape.BoundingBoxShape;
import dev.lazurite.rayon.core.impl.physics.space.body.shape.MinecraftShape;
import dev.lazurite.rayon.core.impl.physics.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.physics.space.util.Clump;
import dev.lazurite.transporter.api.Disassembler;
import dev.lazurite.transporter.api.buffer.PatternBuffer;
import dev.lazurite.transporter.api.pattern.Pattern;
import net.minecraft.block.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.*;

/**
 * This class is used primarily by {@link MinecraftSpace} in order
 * to load and unload blocks from the simulation. The reason not every block is loaded is
 * because it is too resource intensive to track thousands of blocks within the physics
 * simulation. Instead, only a set amount are made available within the world at a time.
 * @see MinecraftSpace
 */
public final class TerrainManager {
    private final List<BlockRigidBody> toKeep = Lists.newArrayList();
    private final MinecraftSpace space;

    public TerrainManager(MinecraftSpace space) {
        this.space = space;
    }

    /**
     * Loads an individual element's block area into the physics simulation. This
     * is also where each block's {@link BlockRigidBody} object is instantiated
     * and properties such as position, shape, friction, etc. are applied here.
     * @param rigidBody the rigid body to be loaded
     * @param box the {@link Box} area around the element to search for blocks within
     */
    public void load(MinecraftRigidBody rigidBody, Box box) {
        World world = space.getWorld();
        Clump clump = new Clump(world, box);

        if (rigidBody.isActive()) {
            clump.getData().forEach(blockInfo -> {
                BlockPos blockPos = blockInfo.getBlockPos();
                BlockState blockState = blockInfo.getBlockState();

                float friction = 0.5f; // 1.0f
                float restitution = 0.25f;
                boolean collidable = !blockState.getBlock().canMobSpawnInside();

                if (blockState.getBlock() instanceof IceBlock) {
                    friction = 0.05F;
                } else if (blockState.getBlock() instanceof SlimeBlock) {
                    friction = 3.0F;
                    restitution = 3.0F;
                } else if (blockState.getBlock() instanceof HoneyBlock || blockState.getBlock() instanceof SoulSandBlock) {
                    friction = 3.0F;
                }

                /* Apply custom block properties */
                Identifier blockId = Registry.BLOCK.getId(blockState.getBlock());
                if (!blockId.getNamespace().equals("minecraft")) {
                    RayonCore.BlockProperties props = RayonCore.getBlockProps().get(blockId);

                    if (props != null) {
                        collidable = props.collidable();

                        if (props.friction() >= 0) {
                            friction = props.friction();
                        }

                        if (props.restitution() >= 0) {
                            restitution = props.restitution();
                        }
                    }
                }

                /* Check if the block is solid or not */
                if (collidable) {
                    var blockRigidBody = findBlockAtPos(space, blockPos);

                    /* Make a new rigid body if there isn't already one */
                    if (blockRigidBody == null) {
                        var voxel = blockState.getCollisionShape(world, blockPos);
                        MinecraftShape shape = voxel.isEmpty() ? MinecraftShape.of(new Box(-0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f)) : MinecraftShape.of(voxel.getBoundingBox());
                        blockRigidBody = new BlockRigidBody(blockState, blockPos, space, shape, friction, restitution);
                    }

                    /* Make a pattern shape if applicable */
                    if (!blockState.isFullCube(world, blockPos)) {
                        Pattern pattern;

                        if (world.isClient()) {
                            var transformation = new MatrixStack();
                            transformation.scale(0.95f, 0.95f, 0.95f);
                            transformation.translate(-0.5f, -0.5f, -0.5f);

                            var blockEntity = world.getBlockEntity(blockPos);

                            try {
                                if (blockEntity != null) {
                                    pattern = Disassembler.getBlockEntity(blockEntity, transformation);
                                } else {
                                    pattern = Disassembler.getBlock(blockState, transformation);
                                }
                            } catch (Exception e) {
                                pattern = null;
                            }
                        } else {
                            pattern = PatternBuffer.getPatternBuffer(world).get(Registry.BLOCK.getId(blockState.getBlock()));
                        }

                        if (pattern != null && !(blockRigidBody.getCollisionShape() instanceof MinecraftShape)) {
                            blockRigidBody.setCollisionShape(MinecraftShape.of(pattern));

                            if (world.isClient()) {
                                PatternBuffer.getPatternBuffer(world).put(pattern);
                            }
                        }
                    }

                    if (!space.getRigidBodyList().contains(blockRigidBody)) {
                        space.addCollisionObject(blockRigidBody);
                    }

                    toKeep.add(blockRigidBody);
                }
            });
        }

        if (!clump.equals(rigidBody.getClump())) {
            rigidBody.activate();
        }

        rigidBody.setClump(clump);
    }

    /**
     * Prune out any unnecessary blocks from the world during each call
     * to {@link MinecraftSpace#step}. The purpose is to prevent
     * any trailing or residual blocks from being left over from a
     * previous {@link TerrainManager#load} call.
     * <b>Note:</b> This method should only be called after every element
     * has been passed through the loading process. Otherwise, blocks will
     * be removed from the simulation prematurely and cause you a headache.
     * @see TerrainManager#load
     */
    public void purge() {
        List<BlockRigidBody> toRemove = Lists.newArrayList();

        space.getRigidBodiesByClass(BlockRigidBody.class).forEach(body -> {
            if (!toKeep.contains(body)) {
                toRemove.add(body);
            }
        });

        toRemove.forEach(space::removeCollisionObject);
        toKeep.clear();
    }

    public MinecraftSpace getSpace() {
        return space;
    }

    public static BlockRigidBody findBlockAtPos(MinecraftSpace space, BlockPos blockPos) {
        for (BlockRigidBody body : space.getRigidBodiesByClass(BlockRigidBody.class)) {
            if (body.getBlockPos().equals(blockPos)) {
                return body;
            }
        }

        return null;
    }
}
