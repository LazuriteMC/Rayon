package dev.lazurite.api.physics.client;

import dev.lazurite.api.physics.client.physics.PhysicsWorld;
import dev.lazurite.api.physics.network.packet.PhysicsHandlerS2C;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;

/**
 * @author Ethan Johnson
 * @author Patrick Hofmann
 */
@Environment(EnvType.CLIENT)
public class ClientInitializer implements ClientModInitializer {

    /** The running instance of the minecraft client. */
    public static final MinecraftClient client = MinecraftClient.getInstance();

    /** Physics World object */
    public static PhysicsWorld physicsWorld;

    /**
     * Initializes all of the registries and loads the player config.
     */
    @Override
    public void onInitializeClient() {
        PhysicsHandlerS2C.register();
    }
}
