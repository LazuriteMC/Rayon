package dev.lazurite.rayon.core.impl.bullet.collision.space.generator;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.core.impl.bullet.collision.body.ElementRigidBody;
import dev.lazurite.rayon.core.impl.bullet.collision.body.TerrainObject;
import dev.lazurite.rayon.core.impl.bullet.collision.body.shape.MinecraftShape;
import dev.lazurite.rayon.core.impl.bullet.collision.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.bullet.collision.space.generator.util.Clump;
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
public class TerrainGenerator {
    public static void step(MinecraftSpace space) {
        final var toKeep = new ArrayList<TerrainObject>();

        for (var rigidBody : space.getRigidBodiesByClass(ElementRigidBody.class)) {
            if (rigidBody.shouldDoTerrainLoading()) {
                var pos = rigidBody.getPhysicsLocation(new Vector3f());
                var loadDistance = (int) rigidBody.boundingBox(new BoundingBox()).getExtent(new Vector3f()).length() + 1;
                var box = new Box(new BlockPos(pos.x, pos.y, pos.z)).expand(loadDistance);
                toKeep.addAll(checkAround(rigidBody, box));
            }
        }

        for (var terrainObject : space.getTerrainObjects()) {
            if (!toKeep.contains(terrainObject)) {
                space.removeTerrainObject(terrainObject);
            }
        }
    }

    /**
     * Loads an individual element's block area into the physics simulation. This
     * is also where each block's {@link TerrainObject} object is instantiated
     * and properties such as position, shape, friction, etc. are applied here.
     * @param rigidBody the rigid body to be loaded
     * @param box the {@link Box} area around the element to search for blocks within
     * @return a list of {@link TerrainObject}s that were loaded or otherwise should be kept
     */
    public static List<TerrainObject> checkAround(ElementRigidBody rigidBody, Box box) {
        final var space = rigidBody.getSpace();
        final var world = space.getWorld();
        final var clump = new Clump(world, box);

        if (rigidBody.isActive()) {
            clump.getTerrainObjects().forEach(terrainObject -> {
                var blockPos = terrainObject.getBlockPos();

                // Fluid Time :)
                terrainObject.getFluidState().ifPresent(fluidState -> {
                    var fluidBody = (TerrainObject.Fluid) terrainObject.getCollisionObject();
                    var fluidBox= fluidBody.boundingBox(null);

                    // buoyancy
                });

                // Block Time :)
                terrainObject.getBlockState().ifPresent(blockState -> {
                    var blockBody = (TerrainObject.Block) terrainObject.getCollisionObject();

                    /* Make a pattern shape if applicable */
                    if (!blockState.isFullCube(world, blockPos)) {
                        var pattern = Transporter.getPatternBuffer().get(Registry.BLOCK.getId(blockState.getBlock()));

                        if (pattern == null && world.isClient()) {
                            pattern = TerrainGenerator.tryGenerateShape(world, blockPos, blockState);
                        }

                        if (pattern != null && blockBody.getCollisionShape().copyHullVertices().length == 108) {
                            blockBody.setCollisionShape(MinecraftShape.of(pattern));
                        }
                    }
                });
            });
        }

        if (!clump.equals(rigidBody.getClump())) {
            rigidBody.activate();
        }

        rigidBody.setClump(clump);
        return clump.getTerrainObjects();
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
