package dev.lazurite.rayon.impl.bullet.collision.body.entity.packet;

import dev.lazurite.rayon.api.EntityPhysicsElement;
import dev.lazurite.rayon.impl.bullet.collision.body.ElementRigidBody;
import dev.lazurite.rayon.impl.bullet.collision.body.EntityRigidBody;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class EntityRigidBodyPropertiesS2C {
    private final int entityId;
    private final float mass;
    private final float dragCoefficient;
    private final float friction;
    private final float restitution;
    private final boolean terrainLoading;
    private final ElementRigidBody.BuoyancyType buoyancyType;
    private final ElementRigidBody.DragType dragType;
    private final UUID priorityPlayer;

    public EntityRigidBodyPropertiesS2C(EntityRigidBody rigidBody) {
        this(
                rigidBody.getElement().cast().getId(),
                rigidBody.getMass(),
                rigidBody.getDragCoefficient(),
                rigidBody.getFriction(),
                rigidBody.getRestitution(),
                rigidBody.terrainLoadingEnabled(),
                rigidBody.getBuoyancyType(),
                rigidBody.getDragType(),
                rigidBody.getPriorityPlayer() == null ? new UUID(0, 0) : rigidBody.getPriorityPlayer().getUUID());
    }

    public EntityRigidBodyPropertiesS2C(int entityId, float mass, float dragCoefficient, float friction, float restitution, boolean terrainLoading, ElementRigidBody.BuoyancyType buoyancyType, ElementRigidBody.DragType dragType, UUID priorityPlayer) {
        this.entityId = entityId;
        this.mass = mass;
        this.dragCoefficient = dragCoefficient;
        this.friction = friction;
        this.restitution = restitution;
        this.terrainLoading = terrainLoading;
        this.buoyancyType = buoyancyType;
        this.dragType = dragType;
        this.priorityPlayer = priorityPlayer;
    }

    public FriendlyByteBuf encode(FriendlyByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeFloat(mass);
        buf.writeFloat(dragCoefficient);
        buf.writeFloat(friction);
        buf.writeFloat(restitution);
        buf.writeBoolean(terrainLoading);
        buf.writeEnum(buoyancyType);
        buf.writeEnum(dragType);
        buf.writeUUID(priorityPlayer);
        return buf;
    }

    public static EntityRigidBodyPropertiesS2C decode(FriendlyByteBuf buf) {
        return new EntityRigidBodyPropertiesS2C(
                buf.readInt(),
                buf.readFloat(),
                buf.readFloat(),
                buf.readFloat(),
                buf.readFloat(),
                buf.readBoolean(),
                buf.readEnum(ElementRigidBody.BuoyancyType.class),
                buf.readEnum(ElementRigidBody.DragType.class),
                buf.readUUID()
        );
    }

    public static void accept(EntityRigidBodyPropertiesS2C packet, Level level){
        final var entity = level.getEntity(packet.entityId);

        if (entity instanceof EntityPhysicsElement element) {
            final var rigidBody = element.getRigidBody();

            rigidBody.setMass(packet.mass);
            rigidBody.setDragCoefficient(packet.dragCoefficient);
            rigidBody.setFriction(packet.friction);
            rigidBody.setRestitution(packet.restitution);
            rigidBody.setTerrainLoadingEnabled(packet.terrainLoading);
            rigidBody.setBuoyancyType(packet.buoyancyType);
            rigidBody.setDragType(packet.dragType);
            rigidBody.prioritize(rigidBody.getSpace().getLevel().getPlayerByUUID(packet.priorityPlayer));
            rigidBody.activate();
        }
    }
}