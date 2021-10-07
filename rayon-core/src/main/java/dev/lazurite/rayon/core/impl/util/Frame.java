package dev.lazurite.rayon.core.impl.util;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.core.api.PhysicsElement;
import dev.lazurite.rayon.core.impl.bullet.math.Convert;
import dev.lazurite.toolbox.api.math.QuaternionHelper;
import dev.lazurite.toolbox.api.math.VectorHelper;

/**
 * A {@link Frame} can be used for interpolation on the render thread.
 * {@link Frame}s are stored in {@link PhysicsElement}s and are updated
 * each tick.
 */
//TODO Fix library class path
public class Frame {
    private Vector3f prevLocation;
    private Vector3f tickLocation;
    private Quaternion prevRotation;
    private Quaternion tickRotation;

    public Frame() {
        this(new Vector3f(), new Quaternion());
    }

    public Frame(Vector3f location, Quaternion rotation) {
        this.set(location, location, rotation, rotation);
    }

    public void set(Vector3f prevLocation, Vector3f tickLocation, Quaternion prevRotation, Quaternion tickRotation) {
        this.prevLocation = prevLocation;
        this.tickLocation = tickLocation;
        this.prevRotation = prevRotation;
        this.tickRotation = tickRotation;
    }

    public void from(Frame frame) {
        this.set(frame.prevLocation, frame.tickLocation, frame.prevRotation, frame.tickRotation);
    }

    public void from(Frame prevFrame, Vector3f tickLocation, Quaternion tickRotation) {
        this.set(prevFrame.tickLocation, tickLocation, prevFrame.tickRotation, tickRotation);
    }

    public Vector3f getLocation(Vector3f store, float tickDelta) {
        var prevLocation = Convert.toMinecraft(this.prevLocation);
        var tickLocation = Convert.toMinecraft(this.tickLocation);
        var lerp = VectorHelper.lerp(prevLocation, tickLocation, tickDelta);
        return store.set(Convert.toBullet(lerp));
    }

    public Quaternion getRotation(Quaternion store, float tickDelta) {
        var prevRotation = Convert.toMinecraft(this.prevRotation);
        var tickRotation= Convert.toMinecraft(this.tickRotation);
        var slerp = QuaternionHelper.slerp(prevRotation, tickRotation, tickDelta);
        return store.set(Convert.toBullet(slerp));
    }

    public Vector3f getLocationDelta(Vector3f store) {
        store.set(tickLocation.subtract(prevLocation));
        return store;
    }

    public Vector3f getRotationDelta(Vector3f store) {
        final var tickRotation = Convert.toMinecraft(this.tickRotation);
        final var prevRotation = Convert.toMinecraft(this.prevRotation);
        final var tickAngles = QuaternionHelper.toEulerAngles(tickRotation);
        final var prevAngles = QuaternionHelper.toEulerAngles(prevRotation);
        tickAngles.sub(prevAngles);
        store.set(Convert.toBullet(tickAngles));
        return store;
    }
}