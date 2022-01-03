package dev.lazurite.rayon.impl.mixin.common;

import dev.lazurite.rayon.impl.bullet.collision.space.MinecraftSpace;
import dev.lazurite.rayon.impl.bullet.collision.space.storage.SpaceStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * This is how each {@link MinecraftSpace} is stored within its associated {@link Level}.
 * @see SpaceStorage
 */
@Mixin(Level.class)
public class LevelMixin implements SpaceStorage {
    @Unique private MinecraftSpace space;

    @Override
    public void setSpace(MinecraftSpace space) {
        this.space = space;
    }

    @Override
    public MinecraftSpace getSpace() {
        return this.space;
    }

    @Inject(method = "neighborChanged", at = @At("HEAD"))
    public void neighborChanged(BlockPos blockPos, Block block, BlockPos blockPos2, CallbackInfo info) {
        final var space = MinecraftSpace.get((Level) (Object) this);
        space.getChunkCache().loadBlockData(blockPos);
        space.wakeNearbyElementRigidBodies(blockPos);
    }
}