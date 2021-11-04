package dev.lazurite.rayon.core.common.impl.bullet.thread.util;

import dev.lazurite.rayon.core.common.impl.bullet.thread.PhysicsThread;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;

/**
 * A pausable interface for {@link PhysicsThread}. Mainly
 * created since {@link Minecraft} cannot be included
 * directly in the server environment.
 */
public interface Pausable {
    default boolean isPaused() {
        if (FabricLoader.getInstance().getEnvironmentType().equals(EnvType.CLIENT)) {
            return Minecraft.getInstance().isPaused();
        }

        return false;
    }
}
