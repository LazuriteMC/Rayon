package dev.lazurite.rayon.core.impl.util.supplier.world;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a {@link WorldSupplier} which returns a single
 * {@link ClientWorld} object in a {@link List} object.
 */
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

    @Nullable @Override
    public World getWorld(RegistryKey<World> key) {
        if (client.world != null && client.world.getRegistryKey().equals(key)) {
            return client.world;
        }

        return null;
    }
}
