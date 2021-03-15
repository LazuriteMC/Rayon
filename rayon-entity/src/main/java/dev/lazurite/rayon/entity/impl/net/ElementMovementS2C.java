package dev.lazurite.rayon.entity.impl.net;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.core.impl.body.ElementRigidBody;
import dev.lazurite.rayon.core.impl.RayonCore;
import dev.lazurite.rayon.core.impl.util.RayonException;
import dev.lazurite.rayon.entity.api.EntityPhysicsElement;
import dev.lazurite.rayon.core.impl.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.util.math.QuaternionHelper;
import dev.lazurite.rayon.core.impl.util.math.VectorHelper;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

/**
 * This packet syncs movement information from the server to all tracking clients.
 * This way, all players see the same movement from physics objects.
 * @see ElementMovementS2C
 */
public class ElementMovementS2C {
    public static final Identifier PACKET_ID = new Identifier(RayonCore.MODID, "entity_element_movement_s2c");

    public static void accept(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        if (client.world != null) {
            MinecraftSpace space = MinecraftSpace.get(client.world);

            int entityId = buf.readInt();
            Quaternion rotation = QuaternionHelper.fromBuffer(buf);
            Vector3f location = VectorHelper.fromBuffer(buf);
            Vector3f linearVelocity = VectorHelper.fromBuffer(buf);
            Vector3f angularVelocity = VectorHelper.fromBuffer(buf);

            if (space.getThread() != null) {
                space.getThread().execute(() -> {
                    Entity entity = client.world.getEntityById(entityId);

                    if (entity instanceof EntityPhysicsElement) {
                        ElementRigidBody rigidBody = ((EntityPhysicsElement) entity).getRigidBody();
                        rigidBody.setPhysicsRotation(rotation);
                        rigidBody.setPhysicsLocation(location);
                        rigidBody.setLinearVelocity(linearVelocity);
                        rigidBody.setAngularVelocity(angularVelocity);
                    }
                });
            }
        }
    }

    public static void send(EntityPhysicsElement element) {
        if (!(element instanceof Entity)) {
            throw new RayonException("Element must be an entity");
        }

        ElementRigidBody rigidBody = element.getRigidBody();
        PacketByteBuf buf = PacketByteBufs.create();

        buf.writeInt(element.asEntity().getEntityId());
        QuaternionHelper.toBuffer(buf, rigidBody.getPhysicsRotation(new Quaternion()));
        VectorHelper.toBuffer(buf, rigidBody.getPhysicsLocation(new Vector3f()));
        VectorHelper.toBuffer(buf, rigidBody.getLinearVelocity(new Vector3f()));
        VectorHelper.toBuffer(buf, rigidBody.getAngularVelocity(new Vector3f()));

        PlayerLookup.tracking(element.asEntity()).forEach(player -> {
            if (!player.equals(rigidBody.getPriorityPlayer())) {
                ServerPlayNetworking.send(player, PACKET_ID, buf);
            }
        });
    }
}
