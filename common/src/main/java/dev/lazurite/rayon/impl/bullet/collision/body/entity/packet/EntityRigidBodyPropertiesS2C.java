package dev.lazurite.rayon.impl.bullet.collision.body.entity.packet;

import dev.lazurite.rayon.api.EntityPhysicsElement;
import dev.lazurite.rayon.impl.bullet.collision.body.ElementRigidBody;
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
    private final boolean terrainLoading;
    private final ElementRigidBody.DragType dragType;
    private final boolean dragForces;
    private final boolean buoyantForces;
    private final UUID priorityPlayer;

    public EntityRigidBodyPropertiesS2C(EntityRigidBody rigidBody) {
        this(
                rigidBody.getElement().cast().getId(),
                rigidBody.getMass(),
                rigidBody.getDragCoefficient(),
                rigidBody.getFriction(),
                rigidBody.getRestitution(),
                rigidBody.terrainLoadingEnabled(),
                rigidBody.getDragType(),
                rigidBody.dragForcesEnabled(),
                rigidBody.buoyantForcesEnabled(),
                rigidBody.getPriorityPlayer() == null ? new UUID(0, 0) : rigidBody.getPriorityPlayer().getUUID());
    }

    public EntityRigidBodyPropertiesS2C(int entityId, float mass, float dragCoefficient, float friction, float restitution, boolean terrainLoading, ElementRigidBody.DragType dragType, boolean dragForces, boolean buoyantForces, UUID priorityPlayer) {
        this.entityId = entityId;
        this.mass = mass;
        this.dragCoefficient = dragCoefficient;
        this.friction = friction;
        this.restitution = restitution;
        this.terrainLoading = terrainLoading;
        this.dragType = dragType;
        this.dragForces = dragForces;
        this.buoyantForces = buoyantForces;
        this.priorityPlayer = priorityPlayer;
    }

    public FriendlyByteBuf encode(FriendlyByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeFloat(mass);
        buf.writeFloat(dragCoefficient);
        buf.writeFloat(friction);
        buf.writeFloat(restitution);
        buf.writeBoolean(terrainLoading);
        buf.writeEnum(dragType);
        buf.writeBoolean(dragForces);
        buf.writeBoolean(buoyantForces);
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
                buf.readEnum(ElementRigidBody.DragType.class),
                buf.readBoolean(),
                buf.readBoolean(),
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
            rigidBody.setDragType(packet.dragType);
            rigidBody.setDragForcesEnabled(packet.dragForces);
            rigidBody.setBuoyantForcesEnabled(packet.buoyantForces);
            rigidBody.prioritize(rigidBody.getSpace().getLevel().getPlayerByUUID(packet.priorityPlayer));
            rigidBody.activate();
        }
    }
}