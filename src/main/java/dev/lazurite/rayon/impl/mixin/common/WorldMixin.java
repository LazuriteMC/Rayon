package dev.lazurite.rayon.impl.mixin.common;

import dev.lazurite.rayon.impl.bullet.space.MinecraftSpace;
import dev.lazurite.rayon.impl.util.space.SpaceStorage;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(World.class)
public class WorldMixin implements SpaceStorage {
    @Unique private MinecraftSpace space;

    @Override
    public void setSpace(MinecraftSpace space) {
        this.space = space;
    }

    @Override
    public MinecraftSpace getSpace() {
        return this.space;
    }
}
