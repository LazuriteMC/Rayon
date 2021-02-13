package dev.lazurite.rayon.impl.bullet.body.type;

import net.minecraft.util.math.BlockPos;

public interface TerrainLoadingBody {
    BlockPos getBlockPos();
    int getLoadDistance();
}
