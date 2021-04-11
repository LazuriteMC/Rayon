package dev.lazurite.rayon.core.impl.util.supplier.player;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

/**
 * This was created because {@link MinecraftClient} is not
 * allowed in server-side code.
 */
@Environment(EnvType.CLIENT)
public interface ClientPlayerSupplier {
    static ClientPlayerEntity get() {
        return MinecraftClient.getInstance().player;
    }
}
