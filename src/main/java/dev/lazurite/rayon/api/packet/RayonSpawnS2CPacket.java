package dev.lazurite.rayon.api.packet;

import dev.lazurite.rayon.physics.body.entity.EntityRigidBody;
import dev.lazurite.rayon.physics.helper.math.QuaternionHelper;
import dev.lazurite.rayon.physics.helper.math.VectorHelper;
import dev.lazurite.rayon.util.exception.RayonSpawnException;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.registry.Registry;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import java.io.IOException;
import java.util.UUID;

public class RayonSpawnS2CPacket implements Packet<ClientPlayPacketListener> {
    private Entity entity;
    private EntityRigidBody body;
    private EntityType<?> entityType;
    private int entityId;
    private UUID uuid;
    private Quat4f orientation;
    private Vector3f position;
    private Vector3f linearVelocity;
    private Vector3f angularVelocity;

    public RayonSpawnS2CPacket(Entity entity) {
        if (!EntityRigidBody.is(entity)) {
            throw new RayonSpawnException("The given entity is not registered.");
        }

        this.entity = entity;
        this.body = EntityRigidBody.get(entity);
        this.entityType = entity.getType();
        this.entityId = entity.getEntityId();
        this.uuid = entity.getUuid();
        this.orientation = body.getOrientation(new Quat4f());
        this.position = body.getCenterOfMassPosition(new Vector3f());
        this.linearVelocity = body.getLinearVelocity(new Vector3f());
        this.angularVelocity = body.getAngularVelocity(new Vector3f());
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        entityType = Registry.ENTITY_TYPE.get(buf.readVarInt());
        entityId = buf.readInt();
        uuid = buf.readUuid();

        orientation = QuaternionHelper.fromBuffer(buf);
        position = VectorHelper.fromBuffer(buf);
        linearVelocity = VectorHelper.fromBuffer(buf);
        angularVelocity = VectorHelper.fromBuffer(buf);
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeVarInt(Registry.ENTITY_TYPE.getRawId(entity.getType()));
        buf.writeInt(entity.getEntityId());
        buf.writeUuid(entity.getUuid());

        QuaternionHelper.toBuffer(buf, body.getOrientation(new Quat4f()));
        VectorHelper.toBuffer(buf, body.getCenterOfMassPosition(new Vector3f()));
        VectorHelper.toBuffer(buf, body.getLinearVelocity(new Vector3f()));
        VectorHelper.toBuffer(buf, body.getAngularVelocity(new Vector3f()));
    }

    @Override
    public void apply(ClientPlayPacketListener listener) {
        ClientWorld world = MinecraftClient.getInstance().world;
        entity = entityType.create(world);
        entity.setEntityId(entityId);
        entity.setUuid(uuid);
        entity.updatePosition(position.x, position.y, position.z);

        body.setOrientation(orientation);
        body.setPosition(position);
        body.setLinearVelocity(linearVelocity);
        body.setAngularVelocity(angularVelocity);

        world.addEntity(entityId, entity);
    }
}
