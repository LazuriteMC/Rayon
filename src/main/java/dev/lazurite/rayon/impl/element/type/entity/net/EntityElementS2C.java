package dev.lazurite.rayon.impl.element.type.entity.net;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.impl.Rayon;
import dev.lazurite.rayon.api.element.PhysicsElement;
import dev.lazurite.rayon.impl.element.ElementRigidBody;
import dev.lazurite.rayon.impl.util.math.QuaternionHelper;
import dev.lazurite.rayon.impl.util.math.VectorHelper;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class EntityElementS2C {
    public static final Identifier PACKET_ID = new Identifier(Rayon.MODID, "entity_element_s2c");

    public static void accept(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        if (client.world != null) {
            int entityId = buf.readInt();
            float mass = buf.readFloat();
            float dragCoefficient = buf.readFloat();
            float friction = buf.readFloat();
            float restitution = buf.readFloat();
            Quaternion rotation = QuaternionHelper.fromBuffer(buf);
            Vector3f location = VectorHelper.fromBuffer(buf);
            Vector3f linearVelocity = VectorHelper.fromBuffer(buf);
            Vector3f angularVelocity = VectorHelper.fromBuffer(buf);

            Rayon.THREAD.get(client.world).execute(space -> {
                Entity entity = client.world.getEntityById(entityId);

                if (entity instanceof PhysicsElement) {
                    ElementRigidBody rigidBody = ((PhysicsElement) entity).getRigidBody();
                    rigidBody.setMass(mass);
                    rigidBody.setDragCoefficient(dragCoefficient);
                    rigidBody.setFriction(friction);
                    rigidBody.setRestitution(restitution);
                    rigidBody.setPhysicsRotation(rotation);
                    rigidBody.setPhysicsLocation(location);
                    rigidBody.setLinearVelocity(linearVelocity);
                    rigidBody.setAngularVelocity(angularVelocity);
                }
            });
        }
    }

    public static void send(PhysicsElement element) {
        assert element instanceof Entity : "Element must be an entity.";

        ElementRigidBody rigidBody = element.getRigidBody();
        PacketByteBuf buf = PacketByteBufs.create();

        buf.writeInt(element.asEntity().getEntityId());
        buf.writeFloat(rigidBody.getMass());
        buf.writeFloat(rigidBody.getDragCoefficient());
        buf.writeFloat(rigidBody.getFriction());
        buf.writeFloat(rigidBody.getRestitution());
        QuaternionHelper.toBuffer(buf, rigidBody.getPhysicsRotation(new Quaternion()));
        VectorHelper.toBuffer(buf, rigidBody.getPhysicsLocation(new Vector3f()));
        VectorHelper.toBuffer(buf, rigidBody.getLinearVelocity(new Vector3f()));
        VectorHelper.toBuffer(buf, rigidBody.getAngularVelocity(new Vector3f()));

        PlayerLookup.tracking(element.asEntity()).forEach(player ->
            ServerPlayNetworking.send(player, PACKET_ID, buf)
        );
    }
}
