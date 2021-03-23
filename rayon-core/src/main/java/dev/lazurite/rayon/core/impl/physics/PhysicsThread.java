package dev.lazurite.rayon.core.impl.physics;

import dev.lazurite.rayon.core.api.PhysicsElement;
import dev.lazurite.rayon.core.api.event.PhysicsSpaceEvents;
import dev.lazurite.rayon.core.impl.RayonCoreCommon;
import dev.lazurite.rayon.core.impl.physics.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.physics.space.util.SpaceStorage;
import dev.lazurite.rayon.core.impl.physics.util.supplier.WorldSupplier;
import dev.lazurite.rayon.core.impl.physics.util.thread.Clock;
import dev.lazurite.rayon.core.impl.physics.util.thread.Pausable;
import dev.lazurite.rayon.core.impl.physics.util.thread.ThreadStorage;
import dev.lazurite.rayon.core.impl.util.RayonException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import net.minecraft.util.thread.ReentrantThreadExecutor;
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
public class PhysicsThread extends Thread implements Pausable {
    private final Queue<Runnable> tasks = new ConcurrentLinkedQueue<>();
    private final ReentrantThreadExecutor<? extends Runnable> executor;
    private final WorldSupplier worldSupplier;
    private final Clock clock = new Clock();
    private float stepRate = 1f / 60f;
    private long nextStep;

    private volatile boolean running = true;
    private volatile Throwable throwable;

    public static PhysicsThread get(ReentrantThreadExecutor<? extends Runnable> executor) {
        return ((ThreadStorage) executor).getPhysicsThread();
    }

    public PhysicsThread(ReentrantThreadExecutor<? extends Runnable> executor, WorldSupplier worldSupplier, String name) {
        this.executor = executor;
        this.worldSupplier = worldSupplier;
        this.nextStep = Util.getMeasuringTimeMs() + (long) (stepRate * 1000);
        this.setName(name);
        this.setUncaughtExceptionHandler((thread, throwable) -> {
            this.throwable = throwable;
            this.running = false;
        });

        RayonCoreCommon.LOGGER.info("Starting " + getName());
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

                if (!isPaused()) {
                    /* Run all queued tasks */
                    while (!tasks.isEmpty()) {
                        tasks.poll().run();
                    }

                    for (World world : worldSupplier.getWorlds()) {
                        for (MinecraftSpace space : ((SpaceStorage) world).getSpaces()) {
                            if (!space.isEmpty() || space.isInPresim()) {
                                space.step(clock.get());
                            }
                        }
                    }
                }

                this.clock.reset();
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

    public float getStepRate() {
        return this.stepRate;
    }

    public Clock getClock() {
        return this.clock;
    }

    public WorldSupplier getWorldSupplier() {
        return this.worldSupplier;
    }

    /**
     * Gets the original thread executor. Useful for returning to the main thread,
     * especially server-side where {@link MinecraftServer} isn't always readily
     * available.
     * @return the original {@link ReentrantThreadExecutor}.
     */
    public ReentrantThreadExecutor<? extends Runnable> getThreadExecutor() {
        return this.executor;
    }

    /**
     * Join the thread when the game closes.
     */
    public void destroy() {
        this.running = false;
        RayonCoreCommon.LOGGER.info("Stopping " + getName());

        try {
            this.join();
        } catch (InterruptedException e) {
            RayonCoreCommon.LOGGER.error("Error joining " + getName());
            e.printStackTrace();
        }
    }
}
