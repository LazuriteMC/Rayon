package dev.lazurite.rayon.util;

import com.bulletphysics.linearmath.Clock;
import dev.lazurite.rayon.physics.world.MinecraftDynamicsWorld;

/**
 * A wrapper for {@link Clock}. It only adds the {@link Delta#get}
 * method which returns the time in seconds while also resetting
 * the timer to zero.
 * @see MinecraftDynamicsWorld#step
 */
public class Delta extends Clock {
    public float get() {
        float out = getTimeMicroseconds() / 1000000F;
        reset();
        return out;
    }
}
