package dev.lazurite.rayon.core.impl.bullet.space.supplier.player;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

/**
 * This was created because {@link MinecraftClient} is not
 * allowed in server-side code.
 */
public interface ClientPlayerSupplier {
    static ClientPlayerEntity get() {
        return MinecraftClient.getInstance().player;
    }
}
