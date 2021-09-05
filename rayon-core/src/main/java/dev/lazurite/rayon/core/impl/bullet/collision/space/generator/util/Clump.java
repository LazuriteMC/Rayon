package dev.lazurite.rayon.core.impl.bullet.collision.space.generator.util;

import com.google.common.collect.Lists;
import dev.lazurite.rayon.core.impl.bullet.collision.body.TerrainObject;
import dev.lazurite.rayon.core.impl.bullet.collision.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.util.BlockProps;
import net.minecraft.block.*;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.List;

public class Clump {
    private final List<TerrainObject> terrainObjects = Lists.newArrayList();

    public Clump(World world, Box box) {
        var space = MinecraftSpace.get(world);

        for (int i = (int) box.minX; i < box.maxX; i++) {
            for (int j = (int) box.minY; j < box.maxY; j++) {
                for (int k = (int) box.minZ; k < box.maxZ; k++) {
                    BlockPos blockPos = new BlockPos(i, j, k);
                    BlockView chunk = world.getChunkManager().getChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4);

                    if (chunk != null) {
                        space.getTerrainObjectAt(blockPos).ifPresentOrElse(this.terrainObjects::add, () -> {
                            if (space.getTerrainObjectAt(blockPos).isEmpty()) {
                                var blockState = chunk.getBlockState(blockPos);
                                var fluidState = chunk.getFluidState(blockPos);

                                if (fluidState.getFluid() != Fluids.EMPTY) {
                                    terrainObjects.add(new TerrainObject(space, blockPos, fluidState, 1000f));
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
                                        terrainObjects.add(new TerrainObject(space, blockPos, blockState, friction, restitution));
                                    }
                                }
                            }
                        });
                    }
                }
            }
        }
    }

    public List<TerrainObject> getTerrainObjects() {
        return this.terrainObjects;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Clump) {
            return getTerrainObjects().equals(((Clump) obj).getTerrainObjects());
        }

        return false;
    }
}
