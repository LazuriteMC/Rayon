package dev.lazurite.rayon.core.impl.util.compat;

import com.qouteall.immersive_portals.ClientWorldLoader;
import dev.lazurite.rayon.core.impl.physics.util.supplier.ClientWorldSupplier;
import dev.lazurite.rayon.core.impl.physics.util.supplier.WorldSupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * This {@link WorldSupplier} extends from {@link ClientWorldSupplier}
 * to allow for multiple client worlds in Immersive Portals.
 * @see ClientWorldSupplier
 */
@Environment(EnvType.CLIENT)
public class ImmersiveWorldSupplier extends ClientWorldSupplier {
    public ImmersiveWorldSupplier(MinecraftClient client) {
        super(client);
    }

    @Override
    public List<World> getWorlds() {
        ArrayList<World> out = new ArrayList<>();

        if (ClientWorldLoader.getIsInitialized()) {
            out.addAll(ClientWorldLoader.getClientWorlds());
        }

        return out;
    }

    @Nullable @Override
    public World getWorld(RegistryKey<World> key) {
        if (ClientWorldLoader.getIsInitialized()) {
            return ClientWorldLoader.getWorld(key);
        }

        return null;
    }
}
