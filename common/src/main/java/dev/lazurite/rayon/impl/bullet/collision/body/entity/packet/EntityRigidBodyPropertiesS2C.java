package dev.lazurite.rayon.impl.bullet.collision.body.entity.packet;

import dev.lazurite.rayon.api.EntityPhysicsElement;
import dev.lazurite.rayon.impl.bullet.collision.body.entity.EntityRigidBody;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class EntityRigidBodyPropertiesS2C {
    private final int entityId;
    private final float mass;
    private final float dragCoefficient;
    private final float friction;
    private final float restitution;
    private final boolean doTerrainLoading;
    private final UUID priorityPlayer;

    public EntityRigidBodyPropertiesS2C(EntityRigidBody rigidBody) {
        this(
                rigidBody.getElement().cast().getId(),
                rigidBody.getMass(),
                rigidBody.getDragCoefficient(),
                rigidBody.getFriction(),
                rigidBody.getRestitution(),
                rigidBody.shouldDoTerrainLoading(),
                rigidBody.getPriorityPlayer() == null ? new UUID(0, 0) : rigidBody.getPriorityPlayer().getUUID());
    }

    public EntityRigidBodyPropertiesS2C(int entityId, float mass, float dragCoefficient, float friction, float restitution, boolean doTerrainLoading, UUID priorityPlayer) {
        this.entityId = entityId;
        this.mass = mass;
        this.dragCoefficient = dragCoefficient;
        this.friction = friction;
        this.restitution = restitution;
        this.doTerrainLoading = doTerrainLoading;
        this.priorityPlayer = priorityPlayer;
    }

    public FriendlyByteBuf encode(FriendlyByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeFloat(mass);
        buf.writeFloat(dragCoefficient);
        buf.writeFloat(friction);
        buf.writeFloat(restitution);
        buf.writeBoolean(doTerrainLoading);
        buf.writeUUID(priorityPlayer);
        return buf;
    }

    public static EntityRigidBodyPropertiesS2C decode(FriendlyByteBuf buf) {
        return new EntityRigidBodyPropertiesS2C(buf.readInt(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readBoolean(), buf.readUUID());
    }

    public static void accept(EntityRigidBodyPropertiesS2C packet, Level level){
        final var entity = level.getEntity(packet.entityId);

        if (entity instanceof EntityPhysicsElement element) {
            final var rigidBody = element.getRigidBody();

            rigidBody.setMass(packet.mass);
            rigidBody.setDragCoefficient(packet.dragCoefficient);
            rigidBody.setFriction(packet.friction);
            rigidBody.setRestitution(packet.restitution);
            rigidBody.setDoTerrainLoading(packet.doTerrainLoading);
            rigidBody.prioritize(rigidBody.getSpace().getLevel().getPlayerByUUID(packet.priorityPlayer));
            rigidBody.activate();
        }
    }
}