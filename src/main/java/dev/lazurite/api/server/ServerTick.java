package dev.lazurite.api.server;

import net.fabricmc.fabric.api.event.server.ServerTickCallback;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

/**
 * @author Ethan Johnson
 */
public class ServerTick {
    /**
     * @param server the {@link MinecraftServer} object
     */
    public static void tick(MinecraftServer server) {
        List<ServerPlayerEntity> players = server.getPlayerManager().getPlayerList();
    }

    /**
     * Register the {@link ServerTick#tick(MinecraftServer)} method.
     */
    public static void register() {
        ServerTickCallback.EVENT.register(ServerTick::tick);
    }
}
