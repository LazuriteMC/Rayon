package dev.lazurite.rayon.impl.util.thread;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;

public interface Pausable {
    default boolean isPaused() {
        if (FabricLoader.getInstance().getEnvironmentType().equals(EnvType.CLIENT)) {
            return MinecraftClient.getInstance().isPaused();
        }

        return false;
    }
}
