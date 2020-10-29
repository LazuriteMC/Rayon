package io.lazurite.api.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

@Environment(EnvType.CLIENT)
public class ClientTick {
    /**
     * Called at the beginning of every minecraft tick.
     * @param client the minecraft client instance
     */
    public static void tick(MinecraftClient client) {

    }

    public static void register() {
        ClientTickEvents.START_CLIENT_TICK.register(ClientTick::tick);
    }
}
