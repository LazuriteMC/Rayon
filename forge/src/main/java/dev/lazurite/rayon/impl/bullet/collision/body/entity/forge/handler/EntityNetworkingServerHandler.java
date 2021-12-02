package dev.lazurite.rayon.impl.bullet.collision.body.entity.forge.handler;

import dev.lazurite.rayon.impl.bullet.collision.body.entity.forge.EntityNetworkingImpl;
import dev.lazurite.rayon.impl.bullet.collision.body.entity.packet.EntityRigidBodyMovementBidirectional;
import dev.lazurite.rayon.impl.bullet.thread.PhysicsThread;
import net.minecraftforge.fmllegacy.network.NetworkDirection;

import java.util.Optional;

public class EntityNetworkingServerHandler {
    public static void register() {
        EntityNetworkingImpl.PACKET_HANDLER.registerMessage(0, EntityRigidBodyMovementBidirectional.class, EntityRigidBodyMovementBidirectional::encode, EntityRigidBodyMovementBidirectional::decode,
                (message, context) -> {
                    final var level = context.get().getSender().level;
                    PhysicsThread.get(level).execute(() ->
                            EntityRigidBodyMovementBidirectional.accept(message, level));
                }, Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }
}