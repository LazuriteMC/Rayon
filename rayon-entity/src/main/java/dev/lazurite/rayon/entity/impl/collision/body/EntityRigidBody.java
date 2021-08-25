package dev.lazurite.rayon.entity.impl.collision.body;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.core.impl.bullet.collision.body.ElementRigidBody;
import dev.lazurite.rayon.core.impl.bullet.collision.body.shape.MinecraftShape;
import dev.lazurite.rayon.core.impl.bullet.collision.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.bullet.math.Converter;
import dev.lazurite.rayon.entity.api.EntityPhysicsElement;
import dev.lazurite.rayon.entity.impl.RayonEntity;
import dev.lazurite.toolbox.api.math.QuaternionHelper;
import dev.lazurite.toolbox.api.math.VectorHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;

import java.util.UUID;

public class EntityRigidBody extends ElementRigidBody {
    private PlayerEntity priorityPlayer;
    private boolean dirtyProperties;

    public EntityRigidBody(EntityPhysicsElement element, MinecraftSpace space, MinecraftShape shape, float mass, float dragCoefficient, float friction, float restitution) {
        super(element, space, shape, mass, dragCoefficient, friction, restitution);
    }

    public EntityRigidBody(EntityPhysicsElement element, MinecraftSpace space, MinecraftShape shape) {
        this(element, space, shape, 1.0f, 0.05f, 1.0f, 0.5f);
    }

    /**
     * The simplest way to create a new {@link EntityRigidBody}.
     * @param element the element to base this body around
     */
    public EntityRigidBody(EntityPhysicsElement element) {
        this(element, MinecraftSpace.get(element.asEntity().getEntityWorld()), element.genShape());
    }

    @Override
    public EntityPhysicsElement getElement() {
        return (EntityPhysicsElement) super.getElement();
    }

    public PlayerEntity getPriorityPlayer() {
        return this.priorityPlayer;
    }

    public boolean isPositionDirty() {
        return getFrame() != null &&
                (getFrame().getLocationDelta(new Vector3f()).length() > 0.1f ||
                getFrame().getRotationDelta(new Vector3f()).length() > 0.01f);
    }

    public boolean arePropertiesDirty() {
        return this.dirtyProperties;
    }

    public void sendMovementPacket() {
        var entity = getElement().asEntity();
        var buf = PacketByteBufs.create();

        buf.writeInt(entity.getId());
        QuaternionHelper.toBuffer(buf, Converter.toMinecraft(getPhysicsRotation(new Quaternion())));
        VectorHelper.toBuffer(buf, Converter.toMinecraft(getPhysicsLocation(new Vector3f())));
        VectorHelper.toBuffer(buf, Converter.toMinecraft(getLinearVelocity(new Vector3f())));
        VectorHelper.toBuffer(buf, Converter.toMinecraft(getAngularVelocity(new Vector3f())));

        if (getSpace().isServer()) {
            PlayerLookup.tracking(entity).forEach(player -> {
                if (!player.equals(getPriorityPlayer())) {
                    ServerPlayNetworking.send(player, RayonEntity.MOVEMENT_PACKET, buf);
                }
            });
        } else {
            ClientPlayNetworking.send(RayonEntity.MOVEMENT_PACKET, buf);
        }
    }

    public void sendPropertiesPacket() {
        if (!getSpace().isServer()) return;

        var entity = getElement().asEntity();
        var buf = PacketByteBufs.create();

        this.dirtyProperties = false;
        buf.writeInt(entity.getId());
        buf.writeFloat(getMass());
        buf.writeFloat(getDragCoefficient());
        buf.writeFloat(getFriction());
        buf.writeFloat(getRestitution());
        buf.writeBoolean(shouldDoTerrainLoading());
        buf.writeUuid(getPriorityPlayer() == null ? new UUID(0,  0) : getPriorityPlayer().getUuid());

        PlayerLookup.tracking(entity).forEach(player ->
            ServerPlayNetworking.send(player, RayonEntity.PROPERTIES_PACKET, buf)
        );
    }

    public void prioritize(PlayerEntity priorityPlayer) {
        this.priorityPlayer = priorityPlayer;
        this.dirtyProperties = true;
    }

    @Override
    public void setMass(float mass) {
        super.setMass(mass);
        this.dirtyProperties = true;
    }

    @Override
    public void setDragCoefficient(float dragCoefficient) {
        super.setDragCoefficient(dragCoefficient);
        this.dirtyProperties = true;
    }

    @Override
    public void setFriction(float friction) {
        super.setFriction(friction);
        this.dirtyProperties = true;
    }

    @Override
    public void setRestitution(float restitution) {
        super.setRestitution(restitution);
        this.dirtyProperties = true;
    }

    @Override
    public void setDoTerrainLoading(boolean doTerrainLoading) {
        super.setDoTerrainLoading(doTerrainLoading);
        this.dirtyProperties = true;
    }
}
