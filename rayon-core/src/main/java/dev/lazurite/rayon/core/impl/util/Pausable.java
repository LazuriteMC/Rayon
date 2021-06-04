package dev.lazurite.rayon.core.impl.util;

import dev.lazurite.rayon.core.impl.physics.PhysicsThread;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;

/**
 * A pausable interface for {@link PhysicsThread}. Mainly
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
