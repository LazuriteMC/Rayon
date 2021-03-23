package dev.lazurite.rayon.core.impl.mixin.common;

import com.google.common.collect.Maps;
import dev.lazurite.rayon.core.impl.physics.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.physics.space.util.SpaceStorage;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Where all spaces in a world are stored. By default, there is
 * only a "main" space: {@link MinecraftSpace#MAIN}.
 * @see SpaceStorage
 */
@Mixin(World.class)
public class WorldMixin implements SpaceStorage {
    @Unique private final Map<Identifier, MinecraftSpace> spaces = Maps.newConcurrentMap();

    @Override
    public void putSpace(Identifier identifier, MinecraftSpace space) {
        this.spaces.put(identifier, space);
    }

    @Override
    public MinecraftSpace getSpace(Identifier identifier) {
        return spaces.get(identifier);
    }

    @Override
    public List<MinecraftSpace> getSpaces() {
        return new ArrayList<>(spaces.values());
    }
}
