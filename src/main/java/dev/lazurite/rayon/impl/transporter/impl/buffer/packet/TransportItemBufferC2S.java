package dev.lazurite.rayon.impl.transporter.impl.buffer.packet;

import com.google.common.collect.Lists;
import dev.lazurite.rayon.impl.transporter.api.buffer.PatternBuffer;
import dev.lazurite.rayon.impl.transporter.api.buffer.BufferStorage;
import dev.lazurite.rayon.impl.transporter.api.event.PatternBufferEvents;
import dev.lazurite.rayon.impl.transporter.api.pattern.TypedPattern;
import dev.lazurite.rayon.impl.transporter.impl.buffer.NetworkedPatternBuffer;
import dev.lazurite.rayon.impl.transporter.impl.pattern.BufferEntry;
import dev.lazurite.rayon.impl.transporter.impl.pattern.part.Quad;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.List;

public class TransportItemBufferC2S {
    public static final Identifier PACKET_ID = new Identifier("transporter", "transport_item_buffer_c2s");

    public static void accept(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        List<BufferEntry<Item>> patterns = Lists.newArrayList();
        int patternCount = buf.readInt();

        for (int i = 0; i < patternCount; i++) {
            List<Quad> quads = Lists.newArrayList();
            Item item = buf.readItemStack().getItem();
            int quadCount = buf.readInt();

            for (int j = 0; j < quadCount; j++) {
                quads.add(Quad.deserialize(buf));
            }

            patterns.add(new BufferEntry<>(quads, item));
        }

        server.execute(() -> {
            NetworkedPatternBuffer<Item> buffer = ((BufferStorage) player.getEntityWorld()).getItemBuffer();
            buffer.putAll(patterns);
            PatternBufferEvents.ITEM_BUFFER_UPDATE.invoker().onUpdate(buffer);
        });
    }

    public static void send(PatternBuffer<Item> buffer) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(buffer.size());

        for (TypedPattern<Item> pattern : buffer.getAll()) {
            buf.writeItemStack(new ItemStack(pattern.getIdentifier()));
            buf.writeInt(pattern.getQuads().size());

            for (Quad quad : pattern.getQuads()) {
                quad.serialize(buf);
            }
        }

        ClientPlayNetworking.send(PACKET_ID, buf);
    }
}
