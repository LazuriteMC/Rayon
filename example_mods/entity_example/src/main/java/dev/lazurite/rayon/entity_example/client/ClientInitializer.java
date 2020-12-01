package dev.lazurite.rayon.entity_example.client;

import dev.lazurite.rayon.entity_example.client.render.TestEntityRenderer;
import dev.lazurite.rayon.entity_example.server.ServerInitializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * This is the client-side entrypoint for this mod. It normally
 * contains registries for things like renderers, keybindings, etc.
 * @author Ethan Johnson
 */
@Environment(EnvType.CLIENT)
public class ClientInitializer implements ClientModInitializer {

    /**
     * Similar to {@link ServerInitializer#onInitialize()}, this
     * method is where you can put any code you want to run when
     * the mod is loaded just on the client. In this case, we're
     * registering the {@link TestEntityRenderer}.
     */
    @Override
    public void onInitializeClient() {
        TestEntityRenderer.register();
    }
}
