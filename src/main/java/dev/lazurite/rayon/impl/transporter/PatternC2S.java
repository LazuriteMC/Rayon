package dev.lazurite.rayon.impl.transporter;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class PatternC2S {
    public static final Identifier PACKET_ID = new Identifier("transporter", "pattern_c2s");

    public static void accept(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        Pattern pattern = new Pattern(PatternType.ITEM);
        int size = buf.readInt();

        for (int i = 0; i < size; i++) {
            pattern.vertex(buf.readFloat(), buf.readFloat(), buf.readFloat());
        }

        server.execute(() -> PatternBuffer.getInstance().put(pattern));
    }

    public static void send(Pattern pattern) {
        PacketByteBuf buf = PacketByteBufs.create();

//        List<Vector3f> points = pattern.getQuads();
//        buf.writeInt(points.size());
//
//        for (Vector3f point : points) {
//            buf.writeFloat(point.getX());
//            buf.writeFloat(point.getY());
//            buf.writeFloat(point.getZ());
//        }
    }
}
