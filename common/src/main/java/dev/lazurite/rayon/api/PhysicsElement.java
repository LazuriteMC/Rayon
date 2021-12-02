package dev.lazurite.rayon.api;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.impl.bullet.collision.body.ElementRigidBody;
import dev.lazurite.rayon.impl.bullet.collision.body.shape.MinecraftShape;
import dev.lazurite.rayon.impl.bullet.collision.space.MinecraftSpace;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.Entity;

/**
 * This is the main interface you'll want to implement into your physics object. It provides
 * the basic components that a {@link PhysicsElement} needs in order to behave properly in the
 * {@link MinecraftSpace}.
 * @since 1.0.0
 */
public interface PhysicsElement<T> {
    /**
     * Gets {@link ElementRigidBody} object associated with this element. You should create and
     * store this in your {@link PhysicsElement} implementation in the constructor. You're able
     * to set up the attributes and settings of your rigid body however you like that way.
     * @return the {@link ElementRigidBody}
     */
    ElementRigidBody getRigidBody();

    /**
     * For generating a new {@link MinecraftShape}. Optionally can be overriden
     * in rayon sub-modules to handle default behavior (see entity module).
     * @return the newly created {@link MinecraftShape}
     */
    MinecraftShape genShape();

    /**
     * Mainly used for lerping within your renderer.
     * @param store any vector to store the output in
     * @param tickDelta the delta time between ticks
     * @return the lerped vector
     */
    @Environment(EnvType.CLIENT)
    default Vector3f getPhysicsLocation(Vector3f store, float tickDelta) {
        return getRigidBody().getFrame().getLocation(store, tickDelta);
    }

    /**
     * Mainly used for lerping within your renderer.
     * @param store the quaternion to store the output in
     * @param tickDelta the delta time between ticks
     * @return the "slerped" quaternion
     */
    @Environment(EnvType.CLIENT)
    default Quaternion getPhysicsRotation(Quaternion store, float tickDelta) {
        return getRigidBody().getFrame().getRotation(store, tickDelta);
    }

    /**
     * Returns the object as its generic type.
     * (e.g. {@link EntityPhysicsElement} -> {@link Entity})
     * @return this as {@link T}
     */
    default T cast() {
        return (T) this;
    }
}