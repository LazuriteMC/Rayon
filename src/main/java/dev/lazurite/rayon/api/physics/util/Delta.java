package dev.lazurite.rayon.api.physics.util;

import com.bulletphysics.linearmath.Clock;

public class Delta extends Clock {

    public Delta() {

    }

    public float get() {
        float out = getTimeMicroseconds() / 1000000F;
        reset();
        return out;
    }
}
