package dev.lazurite.rayon.core.api;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.core.impl.thread.space.body.ElementRigidBody;
import dev.lazurite.rayon.core.impl.thread.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.util.math.interpolate.Frame;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;

/**
 * This is the main interface you'll want to implement into your physics object. It provides
 * the basic components that a {@link PhysicsElement} needs in order to behave properly in the
 * {@link MinecraftSpace}.
 */
public interface PhysicsElement {
    /**
     * <b>Warning:</b> This method is run exclusively on the physics thread.<br>
     * This method is for doing things such as applying forces or interacting with
     * the {@link MinecraftSpace} in some way. If you need to perform some other
     * operation that doesn't have to do with physics {@link Entity#tick()} will suffice.
     * @param space the {@link MinecraftSpace} that the {@link ElementRigidBody} is in
     */
    void step(MinecraftSpace space);

    /**
     * Gets {@link ElementRigidBody} object associated with this element. You should create and
     * store this in your {@link PhysicsElement} implementation in the constructor. You're able
     * to set up the attributes and settings of your rigid body however you like that way. Examples
     * of what you can modify include:
     * <ul>
     *     <li>Position</li>
     *     <li>Rotation</li>
     *     <li>Linear Velocity</li>
     *     <li>Angular Velocity</li>
     *     <li>Mass</li>
     *     <li>Drag</li>
     *     <li>Friction</li>
     *     <li>Restitution</li>
     *     <li>Collision Shape</li>
     *     <li>Priority Player</li>
     * </ul>
     * @return the {@link ElementRigidBody}
     */
    ElementRigidBody getRigidBody();

    /**
     * This is called whenever the element is loaded into the simulation. Any
     * initial values you want set (e.g. position, rotation, velocity, etc.)
     * should be set here.
     * @see MinecraftSpace#load(PhysicsElement) 
     */
    void reset();

    /**
     * @return whether or not this physics element is in noclip mode
     */
    default boolean isInNoClip() {
        return false;
    }

    /**
     * Mainly used for lerping within your renderer, this method returns
     * a lerped location vector based on the provided tick delta.
     * @param store any vector to store the output in
     * @param tickDelta minecraft tick delta
     * @return the lerped vector
     */
    @Environment(EnvType.CLIENT)
    default Vector3f getPhysicsLocation(Vector3f store, float tickDelta) {
        Frame frame = getRigidBody().getFrame();

        if (frame != null) {
            store.set(frame.getLocation(new Vector3f(),  tickDelta));
        }

        return store;
    }

    /**
     * Mainly used for lerping within your renderer, this method returns
     * a "slerped" rotation quaternion based on the provided tick delta.
     * @param store the quaternion to store the output in
     * @param tickDelta minecraft tick delta
     * @return the "slerped" quaternion
     */
    @Environment(EnvType.CLIENT)
    default Quaternion getPhysicsRotation(Quaternion store, float tickDelta) {
        Frame frame = getRigidBody().getFrame();

        if (frame != null) {
            store.set(frame.getRotation(new Quaternion(), tickDelta));
        }

        return store;
    }
}
