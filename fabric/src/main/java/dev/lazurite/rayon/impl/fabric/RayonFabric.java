package dev.lazurite.rayon.impl.fabric;

import dev.lazurite.rayon.impl.Rayon;
import dev.lazurite.rayon.impl.fabric.dev.RayonDevClientFabric;
import dev.lazurite.rayon.impl.fabric.dev.RayonDevFabric;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class RayonFabric implements ModInitializer, ClientModInitializer {
    @Override
    public void onInitialize() {
        Rayon.intialize();

        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            RayonDevFabric.init();
        }
    }

    @Override
    public void onInitializeClient() {
        Rayon.initializeClient();

        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            RayonDevClientFabric.init();
        }
    }
}