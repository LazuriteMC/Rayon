package dev.lazurite.rayon.core.impl.util.compat;

import com.qouteall.immersive_portals.ClientWorldLoader;
import dev.lazurite.rayon.core.impl.thread.supplier.WorldSupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public final class ImmersiveWorldSupplier implements WorldSupplier {
    @Override
    public List<World> getWorlds() {
        return new ArrayList<>(ClientWorldLoader.getClientWorlds());
    }

    @Override
    public World getWorld(RegistryKey<World> key) {
        return ClientWorldLoader.getWorld(key);
    }
}
