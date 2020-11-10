package dev.lazurite.api.client;

import dev.lazurite.api.client.physics.PhysicsWorld;
import dev.lazurite.api.network.packet.*;
import dev.lazurite.api.network.tracker.Config;
import dev.lazurite.api.network.tracker.ConfigFile;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for loading all of the client-side
 * registries and configurations.
 */
@Environment(EnvType.CLIENT)
public class LazuriteClient implements ClientModInitializer {

    /** The running instance of the minecraft client. */
    public static final MinecraftClient client = MinecraftClient.getInstance();

    /** Whether or not the connected server is modded. */
    public static final List<String> remoteLazuriteMods = new ArrayList<>();

    /** Physics World object */
    public static PhysicsWorld physicsWorld;

    /** The player's config */
    public static final String CONFIG_NAME = "physics.properties";
    public static Config config;

    /**
     * Initializes all of the registries and loads the player config.
     */
    @Override
    public void onInitializeClient() {
        GLFW.glfwInit(); // forcefully initializes GLFW

        config = ConfigFile.readConfig(CONFIG_NAME);

        ConfigCommandS2C.register();
        ConfigValueS2C.register();

        ModdedServerS2C.register();
        PhysicsHandlerS2C.register();
    }
}
