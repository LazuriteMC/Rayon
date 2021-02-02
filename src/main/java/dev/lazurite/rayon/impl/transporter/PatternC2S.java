package dev.lazurite.rayon.impl.transporter;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.List;

public class PatternC2S {
    public static final Identifier PACKET_ID = new Identifier("transporter", "pattern_c2s");

    public static void accept(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        Pattern pattern = new Pattern(PatternType.ITEM);

        Identifier identifier = buf.readIdentifier();
        int size = buf.readInt();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < 4; j++) {
                pattern.vertex(buf.readFloat(), buf.readFloat(), buf.readFloat());
            }
        }

        server.execute(() -> {
            PatternBuffer.getInstance().put(identifier, pattern);
        });
    }

    public static void send(Identifier identifier, Pattern pattern) {
        PacketByteBuf buf = PacketByteBufs.create();
        List<Pattern.Quad> quads = pattern.getQuads();

        buf.writeIdentifier(identifier);
        buf.writeInt(quads.size());

        for (Pattern.Quad quad : quads) {
            for (Vector3f point : quad.getPoints()) {
                buf.writeFloat(point.getX());
                buf.writeFloat(point.getY());
                buf.writeFloat(point.getZ());
            }
        }

        ClientPlayNetworking.send(PACKET_ID, buf);
    }
}
