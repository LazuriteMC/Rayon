package dev.lazurite.rayon.impl.bullet.collision.body.entity.forge;

import dev.lazurite.rayon.impl.Rayon;
import dev.lazurite.rayon.impl.bullet.collision.body.entity.EntityNetworking;
import dev.lazurite.rayon.impl.bullet.collision.body.entity.EntityRigidBody;
import dev.lazurite.rayon.impl.bullet.collision.body.entity.packet.EntityRigidBodyMovementBidirectional;
import dev.lazurite.rayon.impl.bullet.collision.body.entity.packet.EntityRigidBodyPropertiesS2C;
import dev.lazurite.toolbox.api.util.PlayerUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fmllegacy.network.NetworkDirection;
import net.minecraftforge.fmllegacy.network.NetworkRegistry;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;

public class EntityNetworkingImpl {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel MOVEMENT = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Rayon.MODID, "movement"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );
    public static final SimpleChannel PROPERTIES = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Rayon.MODID, "properties"),
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
                    MOVEMENT.sendTo(packet, player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
                }
            });
        } else {
            MOVEMENT.sendToServer(packet);
        }
    }

    /**
     * Implementation in {@link EntityNetworking}
     * @param rigidBody
     */
    public static void sendProperties(EntityRigidBody rigidBody) {
        final var packet = new EntityRigidBodyPropertiesS2C(rigidBody);

        PlayerUtil.tracking(rigidBody.getElement().cast()).forEach(player ->
            PROPERTIES.sendTo(packet, player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT));
    }
}