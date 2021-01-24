package dev.lazurite.rayon.util;

import dev.lazurite.rayon.api.packet.RayonSpawnS2CPacket;
import dev.lazurite.rayon.physics.body.EntityRigidBody;
import dev.lazurite.rayon.physics.helper.math.QuaternionHelper;
import dev.lazurite.rayon.physics.helper.math.VectorHelper;
import dev.lazurite.rayon.util.config.Config;
import dev.lazurite.rayon.util.config.ConfigS2C;
import dev.lazurite.rayon.util.config.settings.GlobalSettings;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.registry.Registry;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import java.util.UUID;

public class PacketCallbacks {
    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(ConfigS2C.PACKET_ID, (client, handler, buf, sender) -> {
            GlobalSettings remoteGlobal = new GlobalSettings(
                    buf.readFloat(),
                    buf.readFloat(),
                    buf.readBoolean()
            );

            client.execute(() -> Config.INSTANCE.setRemoteGlobal(remoteGlobal));
        });

        ClientPlayNetworking.registerGlobalReceiver(RayonSpawnS2CPacket.PACKET_ID, (client, handler, buf, sender) -> {
            EntityType<?> entityType = Registry.ENTITY_TYPE.get(buf.readVarInt());
            int entityId = buf.readInt();
            UUID uuid = buf.readUuid();

            Quat4f orientation = QuaternionHelper.fromBuffer(buf);
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

                EntityRigidBody body = EntityRigidBody.get(entity);
                body.setOrientation(orientation);
                body.setPosition(position);
                body.setLinearVelocity(linearVelocity);
                body.setAngularVelocity(angularVelocity);

                client.world.addEntity(entityId, entity);
            });
        });
    }
}
