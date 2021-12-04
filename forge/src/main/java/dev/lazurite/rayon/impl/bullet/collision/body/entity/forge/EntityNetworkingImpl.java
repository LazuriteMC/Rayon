package dev.lazurite.rayon.impl.bullet.collision.body.entity.forge;

import dev.lazurite.rayon.impl.Rayon;
import dev.lazurite.rayon.impl.bullet.collision.body.entity.EntityNetworking;
import dev.lazurite.rayon.impl.bullet.collision.body.entity.EntityRigidBody;
import dev.lazurite.rayon.impl.bullet.collision.body.entity.packet.EntityRigidBodyMovementBidirectional;
import dev.lazurite.rayon.impl.bullet.collision.body.entity.packet.EntityRigidBodyPropertiesS2C;
import dev.lazurite.toolbox.api.util.PlayerUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class EntityNetworkingImpl {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel PACKET_HANDLER = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Rayon.MODID, "packet_handler"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    /**
     * Implementation in {@link EntityNetworking}
     * @param rigidBody
     */
    public static void sendMovement(EntityRigidBody rigidBody) {
        final var packet = new EntityRigidBodyMovementBidirectional(rigidBody);

        if (rigidBody.getSpace().isServer()) {
            PlayerUtil.tracking(rigidBody.getElement().cast()).forEach(player -> {
                if (!player.equals(rigidBody.getPriorityPlayer())) {
                    PACKET_HANDLER.sendTo(packet, player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
                }
            });
        } else {
            PACKET_HANDLER.sendToServer(packet);
        }
    }

    /**
     * Implementation in {@link EntityNetworking}
     * @param rigidBody
     */
    public static void sendProperties(EntityRigidBody rigidBody) {
        final var packet = new EntityRigidBodyPropertiesS2C(rigidBody);

        PlayerUtil.tracking(rigidBody.getElement().cast()).forEach(player ->
            PACKET_HANDLER.sendTo(packet, player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT));
    }
}