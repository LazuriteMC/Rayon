package dev.lazurite.rayon.core.impl.thread.supplier;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public final class ClientWorldSupplier implements WorldSupplier {
    private final MinecraftClient client;

    public ClientWorldSupplier(MinecraftClient client) {
        this.client = client;
    }

    public List<World> getWorlds() {
        ArrayList<World> out = new ArrayList<>();

        if (client.world != null) {
            out.add(client.world);
        }

        return out;
    }

    public World getWorld(RegistryKey<World> key) {
        if (client.world != null && client.world.getRegistryKey().equals(key)) {
            return client.world;
        }

        return null;
    }
}
