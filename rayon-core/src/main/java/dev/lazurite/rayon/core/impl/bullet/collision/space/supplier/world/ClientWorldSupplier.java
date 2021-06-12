package dev.lazurite.rayon.core.impl.bullet.collision.space.supplier.world;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This is a {@link WorldSupplier} which returns a single
 * {@link ClientWorld} object in a {@link List} object.
 */
@Environment(EnvType.CLIENT)
public record ClientWorldSupplier(MinecraftClient client) implements WorldSupplier {
    @Override
    public List<World> getAll() {
        ArrayList<World> out = new ArrayList<>();

        if (client.world != null) {
            out.add(client.world);
        }

        return out;
    }

    @Override
    public World get(RegistryKey<World> key) {
        if (client.world != null && client.world.getRegistryKey().equals(key)) {
            return client.world;
        }

        return null;
    }

    @Override
    public Optional<World> getOptional(RegistryKey<World> key) {
        return Optional.ofNullable(get(key));
    }
}
