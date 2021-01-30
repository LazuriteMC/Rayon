package dev.lazurite.rayon.api.packet;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.Rayon;
import dev.lazurite.rayon.impl.physics.body.EntityRigidBody;
import dev.lazurite.rayon.impl.physics.helper.math.QuaternionHelper;
import dev.lazurite.rayon.impl.physics.helper.math.VectorHelper;
import dev.lazurite.rayon.impl.util.exception.RayonSpawnException;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.UUID;

/**
 * This custom spawn packet can only be used with physics entities. It isn't required in order to
 * spawn your custom entity but it is highly recommended since it handles the transfer of data such
 * as position, orientation, velocity, etc.<br><br>
 *
 * To use this, just call {@link RayonSpawnS2CPacket#get} within your {@link Entity#createSpawnPacket()} method.
 * @since 1.0.0
 */
public class RayonSpawnS2CPacket {
    public static final Identifier PACKET_ID = new Identifier(Rayon.MODID, "rayon_spawn_s2c_packet");

    public static void accept(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
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

            if (client.world != null) {
                client.world.addEntity(entityId, entity);
            }
        });
    }

    public static Packet<?> get (Entity entity) {
        if (!EntityRigidBody.is(entity)) {
            throw new RayonSpawnException("The given entity is not registered.");
        }

        PacketByteBuf buf = PacketByteBufs.create();
        EntityRigidBody body = Rayon.ENTITY.get(entity);

        buf.writeVarInt(Registry.ENTITY_TYPE.getRawId(entity.getType()));
        buf.writeInt(entity.getEntityId());
        buf.writeUuid(entity.getUuid());
        body.setPhysicsLocation(VectorHelper.vec3dToVector3f(entity.getPos().add(0, body.boundingBox(new BoundingBox()).getYExtent() / 2.0, 0)));

        QuaternionHelper.toBuffer(buf, body.getPhysicsRotation(new Quaternion()));
        VectorHelper.toBuffer(buf, body.getPhysicsLocation(new Vector3f()));
        VectorHelper.toBuffer(buf, body.getLinearVelocity(new Vector3f()));
        VectorHelper.toBuffer(buf, body.getAngularVelocity(new Vector3f()));

        return ServerPlayNetworking.createS2CPacket(PACKET_ID, buf);
    }
}