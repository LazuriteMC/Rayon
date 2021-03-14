package dev.lazurite.rayon.entity.api;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Quaternion;
import dev.lazurite.rayon.core.api.element.PhysicsElement;
import dev.lazurite.rayon.core.impl.util.math.QuaternionHelper;
import dev.lazurite.rayon.core.impl.util.math.VectorHelper;
import net.minecraft.entity.Entity;

public interface EntityPhysicsElement extends PhysicsElement {
    @Override
    default void reset() {
        getRigidBody().setPhysicsLocation(VectorHelper.vec3dToVector3f(asEntity().getPos().add(0, getRigidBody().boundingBox(new BoundingBox()).getYExtent(), 0)));
        getRigidBody().setPhysicsRotation(QuaternionHelper.rotateY(new Quaternion(), -asEntity().yaw));
    }

    @Override
    default boolean isInNoClip() {
        return asEntity().noClip;
    }

    /**
     * Cast the {@link EntityPhysicsElement} as an {@link Entity}.
     * @return the entity
     */
    default Entity asEntity() {
        return (Entity) this;
    }
}
