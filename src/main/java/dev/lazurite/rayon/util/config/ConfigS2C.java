package dev.lazurite.rayon.util.config;

import dev.lazurite.rayon.Rayon;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

/**
 * This packet is used for sending the server's config info to all of the clients.
 * The reason to do this is to force all clients to conform to the server's settings.
 * However, there are some values that the user can change on their own client when
 * they're connected to a remote server such as step rate, block distance, and air
 * resistance type.
 * @see Config
 */
public class ConfigS2C {
    public static final Identifier PACKET_ID = new Identifier(Rayon.MODID, "config_s2c");

    public static void send(ServerPlayerEntity player, Config config) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeFloat(config.getGlobal().getGravity());
        buf.writeFloat(config.getGlobal().getAirDensity());
        buf.writeBoolean(config.getGlobal().isAirResistanceEnabled());
        ServerPlayNetworking.send(player, PACKET_ID, buf);
    }
}
