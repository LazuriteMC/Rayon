package dev.lazurite.rayon.impl.physics.thread;

public class Clock {
    private long startTime;

    public Clock() {
        this.reset();
    }

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
