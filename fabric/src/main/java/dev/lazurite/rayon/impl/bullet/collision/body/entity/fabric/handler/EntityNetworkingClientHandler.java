package dev.lazurite.rayon.impl.bullet.collision.body.entity.fabric.handler;

import dev.lazurite.rayon.impl.bullet.collision.body.entity.fabric.EntityNetworkingImpl;
import dev.lazurite.rayon.impl.bullet.collision.body.entity.packet.EntityRigidBodyMovementBidirectional;
import dev.lazurite.rayon.impl.bullet.collision.body.entity.packet.EntityRigidBodyPropertiesS2C;
import dev.lazurite.rayon.impl.bullet.thread.PhysicsThread;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;

public class EntityNetworkingClientHandler {
    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(EntityNetworkingImpl.MOVEMENT, (minecraft, handler, buf, sender) -> {
            final var level = Minecraft.getInstance().level;
            final var packet = EntityRigidBodyMovementBidirectional.decode(buf);

            PhysicsThread.get(level).execute(() ->
                    EntityRigidBodyMovementBidirectional.accept(packet, level));
        });

        ClientPlayNetworking.registerGlobalReceiver(EntityNetworkingImpl.PROPERTIES, (minecraft, handler, buf, sender) -> {
            final var level = Minecraft.getInstance().level;
            final var packet = EntityRigidBodyPropertiesS2C.decode(buf);

            PhysicsThread.get(level).execute(() ->
                    EntityRigidBodyPropertiesS2C.accept(packet, level));
        });
    }
}