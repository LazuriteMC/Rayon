package dev.lazurite.rayon.impl.element.entity.net;

import dev.lazurite.rayon.api.element.PhysicsElement;
import dev.lazurite.rayon.impl.Rayon;
import dev.lazurite.rayon.impl.bullet.body.ElementRigidBody;
import dev.lazurite.rayon.impl.util.RayonException;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.UUID;

/**
 * This packet syncs rigid body information other than movement info from the server to the client.
 * The reason this information isn't included in {@link EntityElementMovementS2C} is because this
 * information should always be sent from the server to the client and not vice versa.
 */
public class ElementPropertiesS2C {
    public static final Identifier PACKET_ID = new Identifier(Rayon.MODID, "element_properties_s2c");

    public static void accept(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        if (client.world != null) {
            int entityId = buf.readInt();
            float mass = buf.readFloat();
            float dragCoefficient = buf.readFloat();
            float friction = buf.readFloat();
            float restitution = buf.readFloat();
            int blockDistance = buf.readInt();
            boolean doFluidResistance = buf.readBoolean();
            UUID priorityPlayer = buf.readUuid();

            Rayon.SPACE.get(client.world).getThread().execute(() -> {
                Entity entity = client.world.getEntityById(entityId);

                if (entity instanceof PhysicsElement) {
                    ElementRigidBody rigidBody = ((PhysicsElement) entity).getRigidBody();
                    PlayerEntity player = client.world.getPlayerByUuid(priorityPlayer);

                    rigidBody.setMass(mass);
                    rigidBody.setDragCoefficient(dragCoefficient);
                    rigidBody.setFriction(friction);
                    rigidBody.setRestitution(restitution);
                    rigidBody.setEnvironmentLoadDistance(blockDistance);
                    rigidBody.setDoFluidResistance(doFluidResistance);
                    rigidBody.prioritize(player);
                }
            });
        }
    }

    public static void send(PhysicsElement element) {
        if (!(element instanceof Entity)) {
            throw new RayonException("Element must be an entity");
        }

        ElementRigidBody rigidBody = element.getRigidBody();
        PacketByteBuf buf = PacketByteBufs.create();

        buf.writeInt(element.asEntity().getEntityId());
        buf.writeFloat(rigidBody.getMass());
        buf.writeFloat(rigidBody.getDragCoefficient());
        buf.writeFloat(rigidBody.getFriction());
        buf.writeFloat(rigidBody.getRestitution());
        buf.writeInt(rigidBody.getEnvironmentLoadDistance());
        buf.writeBoolean(rigidBody.shouldDoFluidResistance());

        if (rigidBody.getPriorityPlayer() == null) {
            buf.writeUuid(new UUID(0, 0));
        } else {
            buf.writeUuid(rigidBody.getPriorityPlayer().getUuid());
        }

        PlayerLookup.tracking(element.asEntity()).forEach(player ->
            ServerPlayNetworking.send(player, PACKET_ID, buf)
        );
    }
}
