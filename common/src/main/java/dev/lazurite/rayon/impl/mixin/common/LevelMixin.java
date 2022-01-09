package dev.lazurite.rayon.impl.mixin.common;

import dev.lazurite.rayon.impl.bullet.collision.space.MinecraftSpace;
import dev.lazurite.rayon.impl.bullet.collision.space.storage.SpaceStorage;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/**
 * This is how each {@link MinecraftSpace} is stored within its associated {@link Level}.
 * @see SpaceStorage
 */
@Mixin(Level.class)
public class LevelMixin implements SpaceStorage {
    @Unique
    private MinecraftSpace space;

    @Override
    public void setSpace(MinecraftSpace space) {
        this.space = space;
    }

    @Override
    public MinecraftSpace getSpace() {
        return this.space;
    }
}