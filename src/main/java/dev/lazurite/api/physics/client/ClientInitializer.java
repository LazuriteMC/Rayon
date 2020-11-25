package dev.lazurite.api.physics.client;

import dev.lazurite.api.physics.network.packet.PhysicsHandlerS2C;
import dev.lazurite.api.physics.server.ServerInitializer;
import dev.lazurite.api.physics.util.VersionChecker;
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

    /** The version checker */
    private static VersionChecker versionChecker;

    /**
     * Initializes all of the registries and loads the player config.
     */
    @Override
    public void onInitializeClient() {
        versionChecker = VersionChecker.getVersion(ServerInitializer.MODID, ServerInitializer.VERSION, ServerInitializer.URL);
        PhysicsHandlerS2C.register();
    }
}
