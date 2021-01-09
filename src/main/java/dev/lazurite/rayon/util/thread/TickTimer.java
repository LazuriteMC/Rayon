package dev.lazurite.rayon.util.thread;

/**
 * A simple class for keeping track of how many
 * ticks have passed. The main purpose is to hide
 * reusable tick counting code.
 * @author Ethan Johnson
 */
public class TickTimer {
    private final int count;
    private int ticks;

    /**
     * The constructor for the Tick Timer.
     * @param ticks the number of ticks before {@link TickTimer#tick()} returns true
     */
    public TickTimer(int count) {
        this.count = count;
    }

    /**
     * Ticks the counter up. If the counter passes
     * the given amount of ticks, return true. The
     * counter is then reset.
     * @return whether or no the counter has passed the given number of ticks
     */
    public boolean tick() {
        if (ticks > count) {
            ticks = 0;
            return true;
        }

        ++ticks;
        return false;
    }
}