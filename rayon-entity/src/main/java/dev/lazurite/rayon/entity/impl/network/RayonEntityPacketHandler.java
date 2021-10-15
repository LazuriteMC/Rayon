package dev.lazurite.rayon.entity.impl.network;

import dev.lazurite.rayon.entity.impl.RayonEntity;
import dev.lazurite.rayon.entity.impl.network.packets.ElementMovementPacket;
import dev.lazurite.rayon.entity.impl.network.packets.ElementPropertyPacket;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.fmllegacy.network.NetworkDirection;
import net.minecraftforge.fmllegacy.network.NetworkRegistry;
import net.minecraftforge.fmllegacy.network.PacketDistributor;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class RayonEntityPacketHandler {

    public static final PacketDistributor<Tuple<Entity, ServerPlayer>> TRACKING_EXCEPT = new PacketDistributor<>(
            RayonEntityPacketHandler::trackingEntityExcept,
            NetworkDirection.PLAY_TO_CLIENT
    );
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(RayonEntity.MODID, "packet_handler"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private RayonEntityPacketHandler() {
    }

    private static Consumer<Packet<?>> trackingEntityExcept(
            PacketDistributor<Tuple<Entity, ServerPlayer>> tuplePacketDistributor,
            Supplier<Tuple<Entity, ServerPlayer>> tupleSupplier
    ) {
        return p -> {
            Tuple<Entity, ServerPlayer> tuple = tupleSupplier.get();
            final Entity entity = tuple.getA();
            ServerPlayer except = tuple.getB();
            ChunkMap chunkMap = ((ServerChunkCache) entity.getCommandSenderWorld().getChunkSource()).chunkMap;
            chunkMap.entityMap.get(entity.getId()).seenBy.forEach(connection -> {
                if (connection.getPlayer() != except) connection.send(p);
            });
        };
    }

    public static void registerPackets() {
        INSTANCE.registerMessage(0, ElementMovementPacket.class,
                ElementMovementPacket::encode,
                ElementMovementPacket::decode,
                ElementMovementPacket::handle
        );
        INSTANCE.registerMessage(1, ElementPropertyPacket.class,
                ElementPropertyPacket::encode,
                ElementPropertyPacket::decode,
                ElementPropertyPacket::handle
        );
    }
}
