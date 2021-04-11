package dev.lazurite.rayon.core.impl.util.supplier.world;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This {@link WorldSupplier} provides a list of all
 * {@link ServerWorld} objects running on the {@link MinecraftServer}.
 */
public class ServerWorldSupplier implements WorldSupplier {
    private final MinecraftServer server;

    public ServerWorldSupplier(MinecraftServer server) {
        this.server = server;
    }

    @Override
    public List<World> getWorlds() {
        return new ArrayList<>((Collection<? extends World>) server.getWorlds());
    }

    @Nullable @Override
    public World getWorld(RegistryKey<World> key) {
        return server.getWorld(key);
    }
}