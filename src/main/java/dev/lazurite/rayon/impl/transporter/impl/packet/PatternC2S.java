package dev.lazurite.rayon.impl.transporter.impl.packet;

import com.google.common.collect.Lists;
import dev.lazurite.rayon.impl.transporter.api.pattern.Pattern;
import dev.lazurite.rayon.impl.transporter.impl.PatternBufferImpl;
import dev.lazurite.rayon.impl.transporter.impl.pattern.QuadContainer;
import dev.lazurite.rayon.impl.transporter.impl.pattern.part.Quad;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class PatternC2S {
    public static final Identifier PACKET_ID = new Identifier("transporter", "pattern_c2s");

    public static void accept(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        List<Quad> quads = Lists.newArrayList();
        Identifier identifier = buf.readIdentifier();
        int size = buf.readInt();

        for (int i = 0; i < size; i++) {
            List<Vec3d> points = Lists.newArrayList();

            for (int j = 0; j < 4; j++) {
                points.add(new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble()));
            }

            quads.add(new Quad(points));
        }

        server.execute(() -> PatternBufferImpl.getInstance().put(identifier, new QuadContainer(quads)));
    }

    public static void send(Identifier identifier, Pattern pattern) {
        PacketByteBuf buf = PacketByteBufs.create();
        List<Quad> quads = pattern.getQuads();

        buf.writeIdentifier(identifier);
        buf.writeInt(quads.size());

        for (Quad quad : quads) {
            for (Vec3d point : quad.getPoints()) {
                buf.writeDouble(point.getX());
                buf.writeDouble(point.getY());
                buf.writeDouble(point.getZ());
            }
        }

        ClientPlayNetworking.send(PACKET_ID, buf);
    }
}
