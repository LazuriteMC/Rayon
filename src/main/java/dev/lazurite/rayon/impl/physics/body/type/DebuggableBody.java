package dev.lazurite.rayon.impl.physics.body.type;

import com.jme3.math.Vector3f;

public interface DebuggableBody {
    default Vector3f getOutlineColor() {
        return new Vector3f(1.0f, 1.0f, 1.0f);
    }

    default float getOutlineAlpha() {
        return 0.5f;
    }

    default int getDebugLayer() {
        return 0;
    }
}
