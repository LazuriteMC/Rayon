package dev.lazurite.rayon.impl.bullet.collision.space.generator;

import com.jme3.bounding.BoundingBox;
import dev.lazurite.rayon.impl.bullet.collision.space.MinecraftSpace;
import dev.lazurite.rayon.impl.bullet.math.Convert;
import dev.lazurite.rayon.impl.bullet.collision.body.ElementRigidBody;
import dev.lazurite.rayon.impl.bullet.collision.body.terrain.TerrainRigidBody;
import net.minecraft.core.BlockPos;

import java.util.HashSet;

/**
 * Used for loading blocks into the simulation so that rigid bodies can interact with them.
 * @see MinecraftSpace
 */
public class TerrainGenerator {
    private static final BoundingBox b1 = new BoundingBox();

    public static void step(MinecraftSpace space) {
        final var chunkCache = space.getChunkCache();
        final var keep = new HashSet<TerrainRigidBody>();

        for (var rigidBody : space.getRigidBodiesByClass(ElementRigidBody.class)) {
            if (!rigidBody.terrainLoadingEnabled() || !rigidBody.isActive()) {
                continue;
            }

            final var aabb = Convert.toMinecraft(rigidBody.boundingBox(b1)).inflate(0.25f);

            BlockPos.betweenClosedStream(aabb).forEach(blockPos -> {
                chunkCache.getBlockData(blockPos).ifPresent(blockData -> {
                    space.getTerrainObjectAt(blockPos).ifPresentOrElse(keep::add, () -> {
                        final var terrain = TerrainRigidBody.from(blockData);
                        space.addCollisionObject(terrain);
                        keep.add(terrain);
                    });
                });
            });
        }

        space.getTerrainMap().forEach((blockPos, terrain) -> {
            if (!keep.contains(terrain)) {
                space.removeTerrainObjectAt(blockPos);
            }
        });
    }
}