package dev.lazurite.api.network.packet;

import dev.lazurite.api.LazuriteAPI;
import dev.lazurite.api.client.LazuriteClient;
import dev.lazurite.api.network.tracker.ConfigFile;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class ConfigCommandS2C {
    public static final Identifier PACKET_ID = new Identifier(LazuriteAPI.MODID, "config_command_s2c");

    public static void accept(PacketContext context, PacketByteBuf buf) {
        String cmd = buf.readString(32767);

        context.getTaskQueue().execute(() -> {
            if (cmd.equals("write")) {
                ConfigFile.writeConfig(LazuriteClient.config, LazuriteClient.CONFIG_NAME);
            } else if (cmd.equals("revert")) {
                LazuriteClient.config = ConfigFile.readConfig(LazuriteClient.CONFIG_NAME);
            }
        });
    }

    public static void send(PlayerEntity player, String key) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeString(key);
        ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, PACKET_ID, buf);
    }

    public static void register() {
        ClientSidePacketRegistry.INSTANCE.register(PACKET_ID, ConfigCommandS2C::accept);
    }
}
