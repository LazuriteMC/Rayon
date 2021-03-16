package dev.lazurite.rayon.core.impl.util.compat;

import com.qouteall.immersive_portals.ClientWorldLoader;
import dev.lazurite.rayon.core.impl.thread.supplier.ClientWorldSupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public World getWorld(RegistryKey<World> key) {
        if (ClientWorldLoader.getIsInitialized()) {
            return ClientWorldLoader.getWorld(key);
        }

        return null;
    }
}
