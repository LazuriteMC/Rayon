package dev.lazurite.rayon.impl.bullet.collision.body.entity.fabric;

import dev.lazurite.rayon.impl.Rayon;
import dev.lazurite.rayon.impl.bullet.collision.body.entity.EntityNetworking;
import dev.lazurite.rayon.impl.bullet.collision.body.entity.EntityRigidBody;
import dev.lazurite.rayon.impl.bullet.collision.body.entity.packet.EntityRigidBodyMovementBidirectional;
import dev.lazurite.rayon.impl.bullet.collision.body.entity.packet.EntityRigidBodyPropertiesS2C;
import dev.lazurite.toolbox.api.util.PlayerUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;

public class EntityNetworkingImpl {
    public static final ResourceLocation MOVEMENT = new ResourceLocation(Rayon.MODID, "movement");
    public static final ResourceLocation PROPERTIES = new ResourceLocation(Rayon.MODID, "properties");

    /**
     * Implementation in {@link EntityNetworking}
     * @param rigidBody
     */
    public static void sendMovement(EntityRigidBody rigidBody) {
        final var packet = new EntityRigidBodyMovementBidirectional(rigidBody);

        if (rigidBody.getSpace().isServer()) {
            PlayerUtil.tracking(rigidBody.getElement().cast()).forEach(player ->
                    ServerPlayNetworking.send(player, MOVEMENT, packet.encode(PacketByteBufs.create())));
        } else {
            ClientPlayNetworking.send(MOVEMENT, packet.encode(PacketByteBufs.create()));
        }
    }

    /**
     * Implementation in {@link EntityNetworking}
     * @param rigidBody
     */
    public static void sendProperties(EntityRigidBody rigidBody) {
        final var packet = new EntityRigidBodyPropertiesS2C(rigidBody);

        PlayerUtil.tracking(rigidBody.getElement().cast()).forEach(player ->
                ServerPlayNetworking.send(player, PROPERTIES, packet.encode(PacketByteBufs.create())));
    }
}