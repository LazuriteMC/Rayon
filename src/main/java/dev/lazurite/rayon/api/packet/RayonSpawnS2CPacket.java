package dev.lazurite.rayon.api.packet;

import dev.lazurite.rayon.Rayon;
import dev.lazurite.rayon.physics.body.EntityRigidBody;
import dev.lazurite.rayon.physics.helper.math.QuaternionHelper;
import dev.lazurite.rayon.physics.helper.math.VectorHelper;
import dev.lazurite.rayon.util.exception.RayonSpawnException;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
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

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
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
    }

    public static Packet<?> get(Entity entity) {
        if (!EntityRigidBody.is(entity)) {
            throw new RayonSpawnException("The given entity is not registered.");
        }

        PacketByteBuf buf = PacketByteBufs.create();
        EntityRigidBody body = EntityRigidBody.get(entity);

        buf.writeVarInt(Registry.ENTITY_TYPE.getRawId(entity.getType()));
        buf.writeInt(entity.getEntityId());
        buf.writeUuid(entity.getUuid());

        /* If the user didn't set the position of the entity using EntityRigidBody#setPosition, then set it to the entity's position instead */
        if (body.getCenterOfMassPosition(new Vector3f()).equals(new Vector3f(0, 0, 0))) {
            body.setPosition(VectorHelper.vec3dToVector3f(entity.getPos().add(0, 1, 0)));
        }

        QuaternionHelper.toBuffer(buf, body.getOrientation(new Quat4f()));
        VectorHelper.toBuffer(buf, body.getCenterOfMassPosition(new Vector3f()));
        VectorHelper.toBuffer(buf, body.getLinearVelocity(new Vector3f()));
        VectorHelper.toBuffer(buf, body.getAngularVelocity(new Vector3f()));

        return ServerPlayNetworking.createS2CPacket(PACKET_ID, buf);
    }

    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(PACKET_ID, RayonSpawnS2CPacket::accept);
    }
}
