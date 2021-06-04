package dev.lazurite.rayon.core.impl.util.model;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.core.api.PhysicsElement;
import dev.lazurite.rayon.core.impl.util.math.QuaternionHelper;
import dev.lazurite.rayon.core.impl.util.math.VectorHelper;
import net.minecraft.util.math.MathHelper;

/**
 * A {@link Frame} can be used for interpolation on the render thread.
 * {@link Frame}s are stored in {@link PhysicsElement}s and are updated
 * each tick.
 */
public class Frame {
    private Vector3f prevLocation;
    private Vector3f tickLocation;
    private Quaternion prevRotation;
    private Quaternion tickRotation;
    private BoundingBox prevBox;
    private BoundingBox tickBox;

    public Frame() {
        this(new Vector3f(), new Quaternion(), new BoundingBox());
    }

    public Frame(Vector3f location, Quaternion rotation, BoundingBox box) {
        this.set(location, location, rotation, rotation, box, box);
    }

    public void set(Vector3f prevLocation, Vector3f tickLocation, Quaternion prevRotation, Quaternion tickRotation, BoundingBox prevBox, BoundingBox tickBox) {
        this.prevLocation = prevLocation;
        this.tickLocation = tickLocation;
        this.prevRotation = prevRotation;
        this.tickRotation = tickRotation;
        this.prevBox = prevBox;
        this.tickBox = tickBox;
    }

    public void from(Frame frame) {
        this.set(frame.prevLocation, frame.tickLocation, frame.prevRotation, frame.tickRotation, frame.prevBox, frame.tickBox);
    }

    public void from(Frame prevFrame, Vector3f tickLocation, Quaternion tickRotation, BoundingBox tickBox) {
        this.set(prevFrame.tickLocation, tickLocation, prevFrame.tickRotation, tickRotation, prevFrame.tickBox, tickBox);
    }

    public Vector3f getLocation(Vector3f store, float tickDelta) {
        return store.set(VectorHelper.lerp(prevLocation, tickLocation, tickDelta));
    }

    public Quaternion getRotation(Quaternion store, float tickDelta) {
        return store.set(QuaternionHelper.slerp(prevRotation, tickRotation, tickDelta));
    }

    public BoundingBox getBox(BoundingBox store, float tickDelta) {
        store.setXExtent(MathHelper.lerp(tickDelta, prevBox.getXExtent(), tickBox.getXExtent()));
        store.setYExtent(MathHelper.lerp(tickDelta, prevBox.getYExtent(), tickBox.getYExtent()));
        store.setZExtent(MathHelper.lerp(tickDelta, prevBox.getZExtent(), tickBox.getZExtent()));
        return store;
    }

    public Vector3f getLocationDelta(Vector3f store) {
        store.set(tickLocation.subtract(prevLocation));
        return store;
    }

    public Vector3f getRotationDelta(Vector3f store) {
        store.set(QuaternionHelper.toEulerAngles(tickRotation).subtract(QuaternionHelper.toEulerAngles(prevRotation)));
        return store;
    }

    public void reset() {
        this.prevLocation = tickLocation.clone();
        this.prevRotation = tickRotation.clone();
        this.prevBox = tickBox;
    }
}