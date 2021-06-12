package dev.lazurite.rayon.core.impl.bullet.collision.space.supplier.world;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * This {@link WorldSupplier} provides a list of all
 * {@link ServerWorld} objects running on the {@link MinecraftServer}.
 */
public record ServerWorldSupplier(MinecraftServer server) implements WorldSupplier {
    @Override
    public List<World> getAll() {
        return new ArrayList<>((Collection<? extends World>) server.getWorlds());
    }

    @Override
    public World get(RegistryKey<World> key) {
        return server.getWorld(key);
    }

    @Override
    public Optional<World> getOptional(RegistryKey<World> key) {
        return Optional.ofNullable(get(key));
    }
}