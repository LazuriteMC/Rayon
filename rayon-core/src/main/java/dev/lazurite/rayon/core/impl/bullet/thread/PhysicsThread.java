package dev.lazurite.rayon.core.impl.bullet.thread;

import dev.lazurite.rayon.core.api.PhysicsElement;
import dev.lazurite.rayon.core.api.event.PhysicsSpaceEvents;
import dev.lazurite.rayon.core.impl.RayonCore;
import dev.lazurite.rayon.core.impl.util.RayonException;
import dev.lazurite.rayon.core.impl.bullet.space.MinecraftSpace;
import net.minecraft.util.Util;
import net.minecraft.util.thread.ThreadExecutor;
import net.minecraft.world.World;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * In order to access an instance of this, all you need is a {@link World} object. The main way to execute
 * tasks on the physics thread is by called {@link PhysicsThread#execute} which gives you access to the
 * {@link MinecraftSpace} object. There are several other ways to execute on the physics thread including
 * registering an event callback in {@link PhysicsSpaceEvents} or inserting code into your
 * {@link PhysicsElement#step} method.
 * @see PhysicsSpaceEvents
 * @see MinecraftSpace
 */
public class PhysicsThread extends Thread {
    private final Queue<Runnable> tasks = new ConcurrentLinkedQueue<>();
    private final ThreadExecutor<? extends Runnable> executor;
    private float stepRate = 1f / 60f;
    private long nextStep;

    private volatile boolean running = true;
    private volatile Throwable throwable;

    public PhysicsThread(ThreadExecutor<? extends Runnable> executor, String name) {
        this.executor = executor;
        this.nextStep = Util.getMeasuringTimeMs() + (long) (stepRate * 1000);
        this.setUncaughtExceptionHandler((thread, throwable) -> {
            this.throwable = throwable;
            this.running = false;
        });
        this.setName(name);
        this.start();
    }

    /**
     * This checks for any uncaught exception on the physics thread. This
     * allows the error to be returned to the main thread and the game will
     * crash in the usual way.
     */
    public void tick() {
        if (throwable != null) {
            throw new RayonException(
                    "Uncaught exception on " + getName() + ": " + throwable + ".",
                    throwable);
        }
    }

    @Override
    public void run() {
        while (running) {
            if (Util.getMeasuringTimeMs() > nextStep) {
                nextStep = Util.getMeasuringTimeMs() + (long) (stepRate * 1000);

                /* Run all queued tasks */
                while (!tasks.isEmpty()) {
                    tasks.poll().run();
                }

                // world supplier
//                spaces.values().forEach(MinecraftSpace::step);
            }
        }
    }

    /**
     * For queueing up tasks to be executed on this thread. A {@link MinecraftSpace}
     * object is provided within the consumer.
     * @param task the task to run
     */
    public void execute(Runnable task) {
        tasks.add(task);
    }

    public void setStepRate(int stepsPerSecond) {
        this.stepRate = 1 / (float) stepsPerSecond;
    }

    public float getStepRate() {
        return this.stepRate;
    }

    /**
     * @return the thread executor for the original thread (e.g. client or server).
     */
    public ThreadExecutor<? extends Runnable> getThreadExecutor() {
        return this.executor;
    }

    /**
     * Join the thread when the game closes.
     */
    public void destroy() {
        this.running = false;

        try {
            this.join();
        } catch (InterruptedException e) {
            RayonCore.LOGGER.error("Error joining " + getName());
            e.printStackTrace();
        }
    }
}
