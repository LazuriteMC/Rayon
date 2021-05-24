package dev.lazurite.rayon.entity.api;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.core.api.PhysicsElement;
import dev.lazurite.rayon.core.impl.physics.space.body.ElementRigidBody;
import dev.lazurite.rayon.core.impl.util.math.QuaternionHelper;
import dev.lazurite.rayon.core.impl.util.math.VectorHelper;
import dev.lazurite.rayon.entity.impl.RayonEntityCommon;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.registry.Registry;

import java.util.UUID;

public interface EntityPhysicsElement extends PhysicsElement {
    @Override
    default void reset() {
        Vector3f location = VectorHelper.vec3dToVector3f(asEntity().getPos().add(0, getRigidBody().boundingBox(new BoundingBox()).getYExtent(), 0));

        if (Float.isFinite(location.lengthSquared())) {
            getRigidBody().setPhysicsLocation(location);
            getRigidBody().getFrame().set(
                    getRigidBody().getPhysicsLocation(new Vector3f()),
                    getRigidBody().getPhysicsLocation(new Vector3f()),
                    getRigidBody().getPhysicsRotation(new Quaternion()),
                    getRigidBody().getPhysicsRotation(new Quaternion()),
                    getRigidBody().boundingBox(new BoundingBox()),
                    getRigidBody().boundingBox(new BoundingBox())
            );
        }
    }

    /**
     * Cast the {@link EntityPhysicsElement} as an {@link Entity}.
     * @return the entity
     */
    default Entity asEntity() {
        return (Entity) this;
    }

    /**
     * Sends a movement update to either the client or the server depending
     * on which side the entity's world is on.
     */
    default void sendMovementUpdate(boolean reset) {
        PacketByteBuf buf = PacketByteBufs.create();

        buf.writeInt(asEntity().getId());
        buf.writeIdentifier(asEntity().getEntityWorld().getRegistryKey().getValue());
        buf.writeBoolean(reset);

        QuaternionHelper.toBuffer(buf, getRigidBody().getPhysicsRotation(new Quaternion()));
        VectorHelper.toBuffer(buf, getRigidBody().getPhysicsLocation(new Vector3f()));
        VectorHelper.toBuffer(buf, getRigidBody().getLinearVelocity(new Vector3f()));
        VectorHelper.toBuffer(buf, getRigidBody().getAngularVelocity(new Vector3f()));

        if (asEntity().getEntityWorld().isClient()) {
            ClientPlayNetworking.send(RayonEntityCommon.MOVEMENT_UPDATE, buf);
        } else {
            PlayerLookup.tracking(asEntity()).forEach(player -> {
                if (!player.equals(getRigidBody().getPriorityPlayer())) {
                    ServerPlayNetworking.send(player, RayonEntityCommon.MOVEMENT_UPDATE, buf);
                }
            });
        }
    }

    default void sendProperties() {
        ElementRigidBody rigidBody = getRigidBody();
        PacketByteBuf buf = PacketByteBufs.create();

        buf.writeInt(asEntity().getId());
        buf.writeIdentifier(asEntity().getEntityWorld().getRegistryKey().getValue());

        buf.writeFloat(rigidBody.getMass());
        buf.writeFloat(rigidBody.getDragCoefficient());
        buf.writeFloat(rigidBody.getFriction());
        buf.writeFloat(rigidBody.getRestitution());
        buf.writeInt(rigidBody.getEnvironmentLoadDistance());
        buf.writeBoolean(rigidBody.shouldDoFluidResistance());
        buf.writeBoolean(rigidBody.shouldDoTerrainLoading());
        buf.writeUuid(rigidBody.getPriorityPlayer() == null ? new UUID(0,  0) : rigidBody.getPriorityPlayer().getUuid());

        PlayerLookup.tracking(asEntity()).forEach(player -> ServerPlayNetworking.send(player, RayonEntityCommon.PROPERTIES, buf));
        rigidBody.setPropertiesDirty(false);
    }

    default Packet<?> getSpawnPacket() {
        ElementRigidBody rigidBody = getRigidBody();
        PacketByteBuf buf = PacketByteBufs.create();

        buf.writeInt(asEntity().getId());
        buf.writeUuid(asEntity().getUuid());
        buf.writeVarInt(Registry.ENTITY_TYPE.getRawId(asEntity().getType()));
        buf.writeIdentifier(asEntity().getEntityWorld().getRegistryKey().getValue());

        VectorHelper.toBuffer(buf, VectorHelper.vec3dToVector3f(asEntity().getPos()));
        VectorHelper.toBuffer(buf, rigidBody.getLinearVelocity(new Vector3f()));
        VectorHelper.toBuffer(buf, rigidBody.getAngularVelocity(new Vector3f()));
        QuaternionHelper.toBuffer(buf, rigidBody.getPhysicsRotation(new Quaternion()));

        return ServerPlayNetworking.createS2CPacket(RayonEntityCommon.SPAWN, buf);
    }
}
