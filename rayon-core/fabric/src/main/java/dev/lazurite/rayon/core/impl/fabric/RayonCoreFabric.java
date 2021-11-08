package dev.lazurite.rayon.core.impl.fabric;

import dev.lazurite.rayon.core.impl.RayonCore;
import dev.lazurite.rayon.core.impl.RayonCoreClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;

public class RayonCoreFabric implements ModInitializer, ClientModInitializer {
    @Override
    public void onInitialize() {
        RayonCore.init();
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void onInitializeClient() {
        RayonCoreClient.init();
    }
}
