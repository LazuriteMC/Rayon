package dev.lazurite.rayon.impl.util.math.interpolate;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.api.element.PhysicsElement;
import dev.lazurite.rayon.impl.element.entity.hooks.CommonEntityMixin;
import dev.lazurite.rayon.impl.util.math.QuaternionHelper;
import dev.lazurite.rayon.impl.util.math.VectorHelper;

/**
 * A {@link Frame} can be used for interpolation on the render thread.
 * {@link Frame}s are stored in {@link PhysicsElement}s and are updated
 * each tick.
 * @see CommonEntityMixin
 */
public class Frame {
    private final Vector3f prevLocation;
    private final Vector3f tickLocation;
    private final Quaternion prevRotation;
    private final Quaternion tickRotation;

    public Frame(Vector3f prevLocation, Vector3f tickLocation, Quaternion prevRotation, Quaternion tickRotation) {
        this.prevLocation = prevLocation;
        this.tickLocation = tickLocation;
        this.prevRotation = prevRotation;
        this.tickRotation = tickRotation;
    }

    public Frame(Vector3f location, Quaternion rotation) {
        this(location, location, rotation, rotation);
    }

    public Frame(Frame frame, Vector3f location, Quaternion rotation) {
        this(frame.tickLocation, location, frame.tickRotation, rotation);
    }

    public Vector3f getLocation(Vector3f store, float tickDelta) {
        store.set(VectorHelper.lerp(prevLocation, tickLocation, tickDelta));
        return store;
    }

    public Quaternion getRotation(Quaternion store, float tickDelta) {
        store.set(QuaternionHelper.slerp(prevRotation, tickRotation, tickDelta));
        return store;
    }

    public boolean hasLocationChanged() {
        return !tickLocation.equals(prevLocation);
    }

    /**
     * Rounds the quaternions to the nearest thousandth and then proceeds
     * to compare the tickRotation and prevRotation.
     * @return whether or not the rotation has changed
     */
    public boolean hasRotationChanged() {
        Quaternion q1 = new Quaternion(
                Math.round(tickRotation.getX() * 1000f) / 1000f,
                Math.round(tickRotation.getY() * 1000f) / 1000f,
                Math.round(tickRotation.getZ() * 1000) / 1000f,
                Math.round(tickRotation.getW() * 1000) / 1000f);

        Quaternion q2 = new Quaternion(
                Math.round(prevRotation.getX() * 1000f) / 1000f,
                Math.round(prevRotation.getY() * 1000f) / 1000f,
                Math.round(prevRotation.getZ() * 1000) / 1000f,
                Math.round(prevRotation.getW() * 1000) / 1000f);

        return !q1.equals(q2);
    }
}
