package dev.lazurite.rayon.core.fabric;

import dev.lazurite.rayon.core.common.impl.RayonCore;
import dev.lazurite.rayon.core.common.impl.RayonCoreClient;
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
