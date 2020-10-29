package io.lazurite.api.network.packet;

import io.lazurite.api.LazuriteAPI;
import io.lazurite.api.client.LazuriteClient;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ModdedServerS2C {
    public static final Identifier PACKET_ID = new Identifier(LazuriteAPI.MODID, "modded_server_s2c");

    public static void accept(PacketContext context, PacketByteBuf buf) {
        String modid = buf.readString();
        context.getTaskQueue().execute(() -> LazuriteClient.remoteLazuriteMods.add(modid));
    }

    public static void send(ServerPlayerEntity player, String modid) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeString(modid);
        ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, PACKET_ID, buf);
    }

    public static void register() {
        ClientSidePacketRegistry.INSTANCE.register(PACKET_ID, ModdedServerS2C::accept);
    }
}
