package dev.lazurite.rayon.impl.bullet.collision.space.cache;

import com.jme3.math.Vector3f;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.lazurite.rayon.impl.Rayon;
import dev.lazurite.rayon.impl.bullet.collision.body.shape.MinecraftShape;
import dev.lazurite.rayon.impl.bullet.collision.space.MinecraftSpace;
import dev.lazurite.transporter.api.Disassembler;
import dev.lazurite.transporter.api.pattern.Pattern;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public interface ChunkCache {
    static ChunkCache create(MinecraftSpace space) {
        return new SimpleChunkCache(space);
    }

    static boolean isValidBlock(BlockState blockState) {
        if (blockState == null) {
            return false;
        }

        final var block = blockState.getBlock();
        final var properties = Rayon.getBlockProperty(block);

        if (properties != null) {
            return properties.collidable();
        }

        return !blockState.isAir()
                && !block.isPossibleToRespawnInThis()
                && blockState.getFluidState().isEmpty();
    }

    @Environment(EnvType.CLIENT)
    static Pattern genShapeForBlock(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState) {
        final var blockEntity = blockGetter.getBlockEntity(blockPos);
        final var transformation = new PoseStack();
        transformation.scale(0.95f, 0.95f, 0.95f);
        transformation.translate(-0.5f, -0.5f, -0.5f);

        try {
            if (blockEntity != null) {
                return Disassembler.getBlockEntity(blockEntity, transformation);
            } else {
                return Disassembler.getBlock(blockState, transformation);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @ExpectPlatform
    static ResourceLocation getRegistryIdFor(Block block) {
        throw new AssertionError();
    }

    void loadBlockData(BlockPos blockPos);
    void refreshAll();
    Optional<BlockData> getBlockData(BlockPos blockPos);
    Optional<BlockData> getBlockData(Vector3f location);

    record BlockData (BlockPos blockPos, BlockState blockState, MinecraftShape shape) { }
}