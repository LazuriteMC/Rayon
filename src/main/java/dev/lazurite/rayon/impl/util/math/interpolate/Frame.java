package dev.lazurite.rayon.impl.util.math.interpolate;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.api.element.PhysicsElement;
import dev.lazurite.rayon.impl.mixin.common.EntityMixin;
import dev.lazurite.rayon.impl.util.math.QuaternionHelper;
import dev.lazurite.rayon.impl.util.math.VectorHelper;

/**
 * A {@link Frame} can be used for interpolation on the render thread.
 * {@link Frame}s are stored in {@link PhysicsElement}s and are updated
 * each tick.
 * @see EntityMixin
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
}
