package dev.lazurite.rayon.impl.util;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.Rayon;
import dev.lazurite.rayon.api.packet.RayonSpawnS2CPacket;
import dev.lazurite.rayon.impl.physics.body.EntityRigidBody;
import dev.lazurite.rayon.impl.physics.helper.math.QuaternionHelper;
import dev.lazurite.rayon.impl.physics.helper.math.VectorHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.registry.Registry;

import java.util.UUID;

public class PacketCallbacks {
    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(RayonSpawnS2CPacket.PACKET_ID, (client, handler, buf, sender) -> {
            EntityType<?> entityType = Registry.ENTITY_TYPE.get(buf.readVarInt());
            int entityId = buf.readInt();
            UUID uuid = buf.readUuid();

            Quaternion orientation = QuaternionHelper.fromBuffer(buf);
            Vector3f position = VectorHelper.fromBuffer(buf);
            Vector3f linearVelocity = VectorHelper.fromBuffer(buf);
            Vector3f angularVelocity = VectorHelper.fromBuffer(buf);

            client.execute(() -> {
                Entity entity = entityType.create(client.world);
                entity.setEntityId(entityId);
                entity.setUuid(uuid);

                float x = position.x;
                float y = position.y - (float) entity.getBoundingBox().getYLength() / 2.0f;
                float z = position.z;
                entity.updatePosition(x, y, z);
                entity.pitch = QuaternionHelper.getPitch(orientation);
                entity.yaw = QuaternionHelper.getYaw(orientation);

                EntityRigidBody body = Rayon.ENTITY.get(entity);
                body.setPhysicsRotation(orientation);
                body.setPhysicsLocation(position);
                body.setLinearVelocity(linearVelocity);
                body.setAngularVelocity(angularVelocity);

                client.world.addEntity(entityId, entity);
            });
        });
    }
}
