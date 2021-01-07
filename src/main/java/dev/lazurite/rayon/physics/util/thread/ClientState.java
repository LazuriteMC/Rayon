package dev.lazurite.rayon.physics.util.thread;

import net.minecraft.client.MinecraftClient;

public class ClientState {
    public static boolean isPaused() {
        return MinecraftClient.getInstance().isPaused();
    }
}
