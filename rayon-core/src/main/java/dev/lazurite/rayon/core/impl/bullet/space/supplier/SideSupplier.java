package dev.lazurite.rayon.core.impl.bullet.space.supplier;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.thread.ReentrantThreadExecutor;

/**
 * I hate
 */
public interface SideSupplier {
    static boolean isClient(ReentrantThreadExecutor executor) {
        return executor instanceof MinecraftClient;
    }
}
