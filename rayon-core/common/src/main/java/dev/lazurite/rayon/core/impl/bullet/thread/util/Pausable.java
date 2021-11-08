package dev.lazurite.rayon.core.impl.bullet.thread.util;

import dev.architectury.utils.EnvExecutor;
import dev.lazurite.rayon.core.impl.bullet.thread.PhysicsThread;
import net.fabricmc.api.EnvType;
import net.minecraft.client.Minecraft;

/**
 * A pausable interface for {@link PhysicsThread}. Mainly
 * created since {@link Minecraft} cannot be included
 * directly in the server environment.
 */
public interface Pausable {
    default boolean isPaused() {
        return EnvExecutor.getInEnv(EnvType.CLIENT, () -> () -> Minecraft.getInstance().isPaused())
                .orElse(false);
    }
}
