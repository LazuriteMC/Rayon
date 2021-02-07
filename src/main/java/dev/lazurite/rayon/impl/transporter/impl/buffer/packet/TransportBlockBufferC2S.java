package dev.lazurite.rayon.impl.transporter.impl.buffer.packet;

import com.google.common.collect.Lists;
import dev.lazurite.rayon.impl.transporter.api.buffer.BufferStorage;
import dev.lazurite.rayon.impl.transporter.api.event.PatternBufferEvents;
import dev.lazurite.rayon.impl.transporter.api.pattern.TypedPattern;
import dev.lazurite.rayon.impl.transporter.impl.buffer.NetworkedPatternBuffer;
import dev.lazurite.rayon.impl.transporter.impl.pattern.BufferEntry;
import dev.lazurite.rayon.impl.transporter.impl.pattern.part.Quad;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class TransportBlockBufferC2S {
    public static final Identifier PACKET_ID = new Identifier("transporter", "transport_block_buffer_c2s");

    public static void accept(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        List<BufferEntry<BlockPos>> patterns = Lists.newArrayList();
        int patternCount = buf.readInt();

        for (int i = 0; i < patternCount; i++) {
            List<Quad> quads = Lists.newArrayList();
            BlockPos blockPos = buf.readBlockPos();
            int quadCount = buf.readInt();

            for (int j = 0; j < quadCount; j++) {
                quads.add(Quad.deserialize(buf));
            }

            patterns.add(new BufferEntry<>(quads, blockPos));
        }

        server.execute(() -> {
            NetworkedPatternBuffer<BlockPos> buffer = ((BufferStorage) player.getEntityWorld()).getBlockBuffer();
            patterns.forEach(buffer::put);
            PatternBufferEvents.BLOCK_BUFFER_UPDATE.invoker().onUpdate(buffer);
        });
    }

    public static void send(NetworkedPatternBuffer<BlockPos> buffer) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(buffer.size());

        for (TypedPattern<BlockPos> pattern : buffer.getAll()) {
            buf.writeBlockPos(pattern.getIdentifier());
            buf.writeInt(pattern.getQuads().size());

            for (Quad quad : pattern.getQuads()) {
                quad.serialize(buf);
            }
        }

        buffer.setDirty(false);
        ClientPlayNetworking.send(PACKET_ID, buf);
    }
}
