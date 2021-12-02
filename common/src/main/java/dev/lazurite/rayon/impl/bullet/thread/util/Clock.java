package dev.lazurite.rayon.impl.bullet.thread.util;

import dev.lazurite.rayon.impl.bullet.collision.space.MinecraftSpace;

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
        return getTimeMicroseconds() / 1000000F;
    }

    public float getAndReset() {
        float delta = get();
        reset();
        return delta;
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