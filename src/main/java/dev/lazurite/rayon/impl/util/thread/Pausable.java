package dev.lazurite.rayon.impl.util.thread;

import dev.lazurite.rayon.impl.bullet.world.MinecraftSpace;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;

/**
 * A pausable interface for {@link MinecraftSpace}. Mainly
 * created since {@link MinecraftClient} cannot be included
 * directly in the server environment.
 */
public interface Pausable {
    default boolean isPaused() {
        if (FabricLoader.getInstance().getEnvironmentType().equals(EnvType.CLIENT)) {
            return MinecraftClient.getInstance().isPaused();
        }

        return false;
    }
}
