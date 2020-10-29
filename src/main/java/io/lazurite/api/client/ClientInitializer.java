package io.lazurite.api.client;

import io.lazurite.api.network.packet.ModdedServerS2C;
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
public class ClientInitializer implements ClientModInitializer {

    /** The running instance of the minecraft client. */
    public static final MinecraftClient client = MinecraftClient.getInstance();

    /** Whether or not the connected server is modded. */
    public static final List<String> remoteLazuriteMods = new ArrayList<>();

    /** Whether or not the client player should be rendered. */
    public static boolean shouldRenderPlayer = false;

    /**
     * Initializes all of the registries and loads the player config.
     */
    @Override
    public void onInitializeClient() {
        GLFW.glfwInit(); // forcefully initializes GLFW
        ClientTick.register();
        ModdedServerS2C.register();
    }
}
