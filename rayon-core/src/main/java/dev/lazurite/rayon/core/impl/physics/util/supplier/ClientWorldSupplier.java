package dev.lazurite.rayon.core.impl.physics.util.supplier;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ClientWorldSupplier implements WorldSupplier {
    private final MinecraftClient client;

    public ClientWorldSupplier(MinecraftClient client) {
        this.client = client;
    }

    @Override
    public List<World> getWorlds() {
        ArrayList<World> out = new ArrayList<>();

        if (client.world != null) {
            out.add(client.world);
        }

        return out;
    }

    @Override
    public World getWorld(RegistryKey<World> key) {
        if (client.world != null && client.world.getRegistryKey().equals(key)) {
            return client.world;
        }

        return null;
    }
}
