package dev.lazurite.rayon.impl.util.debug;

import com.jme3.math.Vector3f;

public interface Debuggable {
    Vector3f getOutlineColor();
    float getOutlineAlpha();
}
