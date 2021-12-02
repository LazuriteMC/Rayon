package dev.lazurite.rayon.impl.bullet.collision.body.entity.fabric.handler;

import dev.lazurite.rayon.impl.bullet.collision.body.entity.fabric.EntityNetworkingImpl;
import dev.lazurite.rayon.impl.bullet.collision.body.entity.packet.EntityRigidBodyMovementBidirectional;
import dev.lazurite.rayon.impl.bullet.thread.PhysicsThread;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class EntityNetworkingServerHandler {
    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(EntityNetworkingImpl.MOVEMENT, ((server, player, handler, buf, responseSender) -> {
            final var level = player.level;
            final var packet = EntityRigidBodyMovementBidirectional.decode(buf);

            PhysicsThread.get(level).execute(() ->
                    EntityRigidBodyMovementBidirectional.accept(packet, level));
        }));
    }
}