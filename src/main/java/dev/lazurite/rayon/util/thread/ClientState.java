package dev.lazurite.rayon.util.thread;

import net.minecraft.client.MinecraftClient;

public class ClientState {
    public static boolean isPaused() {
        return MinecraftClient.getInstance().isPaused();
    }
}
