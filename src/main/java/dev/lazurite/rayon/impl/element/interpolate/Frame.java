package dev.lazurite.rayon.impl.element.interpolate;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.impl.util.math.QuaternionHelper;
import dev.lazurite.rayon.impl.util.math.VectorHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
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

    public interface Storage {
        void setFrame(Frame frame);
        Frame getFrame();
    }
}
