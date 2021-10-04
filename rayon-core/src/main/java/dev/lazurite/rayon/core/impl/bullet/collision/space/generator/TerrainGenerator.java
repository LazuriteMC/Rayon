package dev.lazurite.rayon.core.impl.bullet.collision.space.generator;

import dev.lazurite.rayon.core.impl.bullet.collision.body.ElementRigidBody;
import dev.lazurite.rayon.core.impl.bullet.collision.body.TerrainObject;
import dev.lazurite.rayon.core.impl.bullet.collision.body.shape.MinecraftShape;
import dev.lazurite.rayon.core.impl.bullet.collision.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.bullet.math.Convert;
import dev.lazurite.rayon.core.impl.util.BlockProps;
import dev.lazurite.toolbox.api.math.VectorHelper;
import dev.lazurite.transporter.Transporter;
import dev.lazurite.transporter.api.Disassembler;
import dev.lazurite.transporter.api.pattern.Pattern;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;

import java.util.*;

/**
 * Used for loading blocks into the simulation so that rigid bodies can interact with them.
 * @see MinecraftSpace
 */
public class TerrainGenerator {
    public static void step(MinecraftSpace space) {
        final var world = space.getWorld();
        final var toKeep = new HashMap<BlockPos, TerrainObject>();

        for (var rigidBody : space.getRigidBodiesByClass(ElementRigidBody.class)) {
            if (!rigidBody.shouldDoTerrainLoading()) {
                continue;
            }

            final var blocks = new ArrayList<BlockPos>();

            final var d = VectorHelper.toVec3d(Convert.toMinecraft(rigidBody.getLinearVelocity(null)));
            final var x = Math.min(Math.abs(d.x), 1);
            final var y = Math.min(Math.abs(d.y), 1);
            final var z = Math.min(Math.abs(d.z), 1);
            final var box = Convert.toMinecraft(rigidBody.boundingBox(null)).expand(0.5f).expand(x, y, z);
            final var pos = rigidBody.getPhysicsLocation(null);
            final var chunk = world.getChunk(ChunkSectionPos.getSectionCoord(pos.x), ChunkSectionPos.getSectionCoord(pos.z));

            for (int i = (int) Math.floor(box.minX); i < Math.ceil(box.maxX); i++) {
                for (int j = (int) Math.floor(box.minY); j < Math.ceil(box.maxY); j++) {
                    for (int k = (int) Math.floor(box.minZ); k < Math.ceil(box.maxZ); k++) {
                        if (box.contains(Vec3d.ofCenter(new BlockPos(i, j, k)))) {
                            final var blockPos = new BlockPos(i, j, k);
                            blocks.add(blockPos);

                            final var terrainObject = rigidBody.getTerrainObjects().get(blockPos);

                            if (terrainObject != null) {
                                terrainObject.getBlockState().ifPresent(blockState -> {
                                    if (!chunk.getBlockState(blockPos).equals(blockState)) {
                                        rigidBody.activate();
                                    }
                                });
                            }
                        }
                    }
                }
            }

            for (var blockPos : blocks) {
                space.getTerrainObjectAt(blockPos).ifPresentOrElse(terrainObject -> {
                    if (rigidBody.isActive() && !chunk.getBlockState(terrainObject.getBlockPos()).getBlock().equals(Blocks.AIR) || !chunk.getFluidState(terrainObject.getBlockPos()).getFluid().equals(Fluids.EMPTY)) {
                        toKeep.put(blockPos, terrainObject);
                    }
                }, () -> {
                    if (rigidBody.isActive()) {
                        var blockState = chunk.getBlockState(blockPos);
                        var fluidState = chunk.getFluidState(blockPos);

                        if (fluidState.getFluid() != Fluids.EMPTY) {
                            var newTerrainObject = new TerrainObject(space, blockPos, fluidState);
                            space.addTerrainObject(newTerrainObject);
                            toKeep.put(blockPos, newTerrainObject);
                        } else if (blockState.getBlock() != Blocks.AIR) {
                            var optional = BlockProps.get(Registry.BLOCK.getId(blockState.getBlock()));

                            float friction = 0.75f;
                            float restitution = 0.25f;
                            boolean collidable = false;

                            if (blockState.getBlock() instanceof IceBlock) {
                                friction = 0.05F;
                            } else if (blockState.getBlock() instanceof SlimeBlock) {
                                friction = 3.0F;
                                restitution = 3.0F;
                            } else if (blockState.getBlock() instanceof HoneyBlock || blockState.getBlock() instanceof SoulSandBlock) {
                                friction = 3.0F;
                            }

                            if (optional.isPresent()) {
                                friction = Math.max(0, optional.get().friction());
                                restitution = Math.max(0, optional.get().restitution());
                                collidable = optional.get().collidable();
                            }

                            if (!blockState.getBlock().canMobSpawnInside() || collidable) {
                                final var newTerrainObject = new TerrainObject(space, blockPos, blockState, friction, restitution);

                                /* Make a pattern shape if applicable */
                                if (!blockState.isFullCube(world, blockPos)) {
                                    var pattern = Transporter.getPatternBuffer().get(Registry.BLOCK.getId(blockState.getBlock()));

                                    if (pattern == null && world.isClient()) {
                                        pattern = TerrainGenerator.tryGenerateShape(world, blockPos, blockState);
                                    }

                                    if (pattern != null && ((MinecraftShape) newTerrainObject.getCollisionObject().getCollisionShape()).copyHullVertices().length == 108) {
                                        newTerrainObject.getCollisionObject().setCollisionShape(MinecraftShape.of(pattern));
                                    }
                                }

                                space.addTerrainObject(newTerrainObject);
                                toKeep.put(blockPos, newTerrainObject);
                            }
                        }
                    }
                });

                if (rigidBody.isActive()) {
                    rigidBody.setTerrainObjects(toKeep);
                }
            }
        }

        for (var terrainObject : space.getTerrainObjects()) {
            if (!toKeep.containsValue(terrainObject)) {
                space.removeTerrainObject(terrainObject);
            }
        }
    }

    @Environment(EnvType.CLIENT)
    private static Pattern tryGenerateShape(BlockView blockView, BlockPos blockPos, BlockState blockState) {
        final var blockEntity = blockView.getBlockEntity(blockPos);
        final var transformation = new MatrixStack();
        transformation.scale(0.95f, 0.95f, 0.95f);
        transformation.translate(-0.5f, -0.5f, -0.5f);

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
