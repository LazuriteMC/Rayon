package dev.lazurite.rayon.entity.api;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.core.api.PhysicsElement;
import dev.lazurite.rayon.core.impl.util.math.QuaternionHelper;
import dev.lazurite.rayon.core.impl.util.math.VectorHelper;
import dev.lazurite.rayon.entity.impl.RayonEntityCommon;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;

public interface EntityPhysicsElement extends PhysicsElement {
    @Override
    default void reset() {
        getRigidBody().setPhysicsLocation(VectorHelper.vec3dToVector3f(asEntity().getPos().add(0, getRigidBody().boundingBox(new BoundingBox()).getYExtent(), 0)));
        getRigidBody().setPhysicsRotation(QuaternionHelper.rotateY(new Quaternion(), -asEntity().yaw));
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
    default void sendMovementUpdate() {
        PacketByteBuf buf = PacketByteBufs.create();

        buf.writeInt(asEntity().getEntityId());
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
}
