package dev.lazurite.rayon.impl.util.environment;

import com.google.common.collect.Lists;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.List;

public class Clump {
    private final List<BlockInfo> blockInfo = Lists.newArrayList();

    public Clump(World world, Box box) {
        for (int i = (int) box.minX; i < box.maxX; i++) {
            for (int j = (int) box.minY; j < box.maxY; j++) {
                for (int k = (int) box.minZ; k < box.maxZ; k++) {
                    BlockPos blockPos = new BlockPos(i, j, k);
                    BlockView chunk = world.getChunkManager().getChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4);

                    if (chunk != null) {
                        blockInfo.add(new BlockInfo(blockPos, chunk.getBlockState(blockPos)));
                    }
                }
            }
        }
    }

    public List<BlockInfo> getData() {
        return this.blockInfo;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Clump) {
            return getData().equals(((Clump) obj).getData());
        }

        return false;
    }

    public static class BlockInfo {
        private final BlockPos blockPos;
        private final BlockState blockState;

        public BlockInfo(BlockPos blockPos, BlockState blockState) {
            this.blockPos = blockPos;
            this.blockState = blockState;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof BlockInfo) {
                return ((BlockInfo) obj).blockState.getBlock() == blockState.getBlock() && ((BlockInfo) obj).blockPos.equals(blockPos);
            }

            return false;
        }

        public BlockPos getBlockPos() {
            return this.blockPos;
        }

        public BlockState getBlockState() {
            return this.blockState;
        }
    }
}
