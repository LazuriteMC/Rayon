package dev.lazurite.api.physics.example.client;

import dev.lazurite.api.physics.example.client.render.TestEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ClientInitializer implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        TestEntityRenderer.register();
    }
}
