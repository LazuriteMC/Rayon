package dev.lazurite.rayon.impl.util.thread;

import dev.lazurite.rayon.impl.bullet.world.MinecraftSpace;

/**
 * A simple clock for keeping track of delta time in milliseconds and microseconds.
 * @see MinecraftSpace
 */
public class Clock {
    private long startTime;

    public Clock() {
        this.reset();
    }

    /**
     * @return seconds since last called
     */
    public float get() {
        float out = getTimeMicroseconds() / 1000000F;
        this.reset();
        return out;
    }

    public void reset() {
        this.startTime = System.nanoTime();
    }

    public long getTimeMilliseconds() {
        return (System.nanoTime() - this.startTime) / 1000000L;
    }

    public long getTimeMicroseconds() {
        return (System.nanoTime() - this.startTime) / 1000L;
    }
}
