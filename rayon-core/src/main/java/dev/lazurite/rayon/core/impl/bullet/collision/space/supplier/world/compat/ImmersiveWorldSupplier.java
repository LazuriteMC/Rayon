package dev.lazurite.rayon.core.impl.bullet.collision.space.supplier.world.compat;

import dev.lazurite.rayon.core.impl.bullet.collision.space.supplier.world.WorldSupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import qouteall.imm_ptl.core.ClientWorldLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This {@link WorldSupplier} allows for multiple {@link ClientWorld}s.
 */
@Environment(EnvType.CLIENT)
public record ImmersiveWorldSupplier(MinecraftClient client) implements WorldSupplier {
    @Override
    public List<World> getAll() {
        ArrayList<World> out = new ArrayList<>();

        if (ClientWorldLoader.getIsInitialized()) {
            out.addAll(ClientWorldLoader.getClientWorlds());
        }

        return out;
    }

    @Override
    public World get(RegistryKey<World> key) {
        if (ClientWorldLoader.getIsInitialized()) {
            return ClientWorldLoader.getWorld(key);
        }

        return null;
    }

    @Override
    public Optional<World> getOptional(RegistryKey<World> key) {
        return Optional.ofNullable(get(key));
    }
}
