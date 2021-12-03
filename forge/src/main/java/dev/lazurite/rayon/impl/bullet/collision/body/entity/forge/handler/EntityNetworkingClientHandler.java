package dev.lazurite.rayon.impl.bullet.collision.body.entity.forge.handler;

import dev.lazurite.rayon.impl.bullet.collision.body.entity.forge.EntityNetworkingImpl;
import dev.lazurite.rayon.impl.bullet.collision.body.entity.packet.EntityRigidBodyMovementBidirectional;
import dev.lazurite.rayon.impl.bullet.collision.body.entity.packet.EntityRigidBodyPropertiesS2C;
import dev.lazurite.rayon.impl.bullet.thread.PhysicsThread;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fmllegacy.network.NetworkDirection;

import java.util.Optional;

public class EntityNetworkingClientHandler {
    public static void register() {
        EntityNetworkingImpl.MOVEMENT.registerMessage(1, EntityRigidBodyMovementBidirectional.class, EntityRigidBodyMovementBidirectional::encode, EntityRigidBodyMovementBidirectional::decode,
                (message, context) -> {
                    final var level = Minecraft.getInstance().level;
                    PhysicsThread.get(level).execute(() -> EntityRigidBodyMovementBidirectional.accept(message, level));
                    context.get().setPacketHandled(true); // forg moment
                }, Optional.of(NetworkDirection.PLAY_TO_CLIENT));

        EntityNetworkingImpl.PROPERTIES.registerMessage(0, EntityRigidBodyPropertiesS2C.class, EntityRigidBodyPropertiesS2C::encode, EntityRigidBodyPropertiesS2C::decode,
                (message, context) -> {
                    final var level = Minecraft.getInstance().level;
                    PhysicsThread.get(level).execute(() -> EntityRigidBodyPropertiesS2C.accept(message, level));
                    context.get().setPacketHandled(true); // forg moment
                }, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }
}