package dev.lazurite.rayon.core.impl.bullet.collision.space.components;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.core.impl.bullet.collision.body.BlockRigidBody;
import dev.lazurite.rayon.core.impl.bullet.collision.body.MinecraftRigidBody;
import dev.lazurite.rayon.core.impl.bullet.collision.body.shape.MinecraftShape;
import dev.lazurite.rayon.core.impl.bullet.collision.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.util.BlockProps;
import dev.lazurite.rayon.core.impl.util.model.Clump;
import dev.lazurite.transporter.Transporter;
import dev.lazurite.transporter.api.Disassembler;
import dev.lazurite.transporter.api.pattern.Pattern;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;

import java.util.*;

/**
 * Used for loading blocks into the simulation so that rigid bodies can interact with them.
 * @see MinecraftSpace
 */
public class TerrainComponent {
    public static void step(MinecraftSpace space) {
        final var toKeep = new ArrayList<BlockRigidBody>();

        for (var rigidBody : space.getRigidBodiesByClass(MinecraftRigidBody.class)) {
            if (rigidBody.shouldDoTerrainLoading()) {
                var pos = rigidBody.getPhysicsLocation(new Vector3f());
                var loadDistance = (int) rigidBody.boundingBox(new BoundingBox()).getExtent(new Vector3f()).length() + 1;
                var box = new Box(new BlockPos(pos.x, pos.y, pos.z)).expand(loadDistance);
                toKeep.addAll(getOrCreateAround(rigidBody, box));
            }
        }

        for (var rigidBody : space.getRigidBodiesByClass(BlockRigidBody.class)) {
            if (!toKeep.contains(rigidBody)) {
                space.removeCollisionObject(rigidBody);
            }
        }
    }

    /**
     * Loads an individual element's block area into the physics simulation. This
     * is also where each block's {@link BlockRigidBody} object is instantiated
     * and properties such as position, shape, friction, etc. are applied here.
     * @param rigidBody the rigid body to be loaded
     * @param box the {@link Box} area around the element to search for blocks within
     * @return a list of {@link BlockRigidBody}s that were loaded or otherwise should be kept
     */
    public static List<BlockRigidBody> getOrCreateAround(MinecraftRigidBody rigidBody, Box box) {
        final var space = rigidBody.getSpace();
        final var world = space.getWorld();
        final var clump = new Clump(world, box);
        final var toKeep = new ArrayList<BlockRigidBody>();

        if (rigidBody.isActive()) {
            clump.getData().forEach(blockInfo -> {
                var blockPos = blockInfo.getBlockPos();
                var blockState = blockInfo.getBlockState();

                var friction = 0.75f;
                var restitution = 0.25f;
                var collidable = !blockState.getBlock().canMobSpawnInside();

                if (blockState.getBlock() instanceof IceBlock) {
                    friction = 0.05F;
                } else if (blockState.getBlock() instanceof SlimeBlock) {
                    friction = 3.0F;
                    restitution = 3.0F;
                } else if (blockState.getBlock() instanceof HoneyBlock || blockState.getBlock() instanceof SoulSandBlock) {
                    friction = 3.0F;
                }

                /* Apply custom block properties */
                var blockId = Registry.BLOCK.getId(blockState.getBlock());
                if (!blockId.getNamespace().equals("minecraft")) {
                    var props = BlockProps.get().get(blockId);

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
                        var shape = voxel.isEmpty() ? null : MinecraftShape.of(voxel.getBoundingBox());
                        blockRigidBody = new BlockRigidBody(blockState, blockPos, space, shape, friction, restitution);
                    }

                    /* Make a pattern shape if applicable */
                    if (!blockState.isFullCube(world, blockPos)) {
                        var pattern = Transporter.getPatternBuffer().get(Registry.BLOCK.getId(blockState.getBlock()));

                        if (pattern == null && world.isClient()) {
                            pattern = tryGenerateShape(world, blockPos, blockState);
                        }

                        if (pattern != null && blockRigidBody.getCollisionShape().getTriangles().size() == 36) {
                            blockRigidBody.setCollisionShape(MinecraftShape.of(pattern));
                        }
                    }

                    if (!blockRigidBody.isInWorld()) {
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
        return toKeep;
    }

    public static BlockRigidBody findBlockAtPos(MinecraftSpace space, BlockPos blockPos) {
        for (var body : space.getRigidBodiesByClass(BlockRigidBody.class)) {
            if (body.getBlockPos().equals(blockPos)) {
                return body;
            }
        }

        return null;
    }

    @Environment(EnvType.CLIENT)
    public static Pattern tryGenerateShape(BlockView blockView, BlockPos blockPos, BlockState blockState) {
        var transformation = new MatrixStack();
        transformation.scale(0.95f, 0.95f, 0.95f);
        transformation.translate(-0.5f, -0.5f, -0.5f);

        var blockEntity = blockView.getBlockEntity(blockPos);

        try {
            if (blockEntity != null) {
                return Disassembler.getBlockEntity(blockEntity, transformation);
            } else {
                return Disassembler.getBlock(blockState, transformation);
            }
        } catch (Exception e) {
            return null;
        }
    }
}
