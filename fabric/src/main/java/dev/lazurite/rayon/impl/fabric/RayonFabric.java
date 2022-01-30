package dev.lazurite.rayon.impl.fabric;

import dev.lazurite.rayon.impl.Rayon;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;

public class RayonFabric implements ModInitializer, ClientModInitializer {
    @Override
    public void onInitialize() {
        Rayon.intialize();
    }

    @Override
    public void onInitializeClient() {
        Rayon.initializeClient();
    }
}