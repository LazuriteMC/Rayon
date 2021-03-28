package dev.lazurite.rayon.core.impl.physics;

import dev.lazurite.rayon.core.api.PhysicsElement;
import dev.lazurite.rayon.core.api.event.PhysicsSpaceEvents;
import dev.lazurite.rayon.core.impl.RayonCoreCommon;
import dev.lazurite.rayon.core.impl.physics.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.physics.util.supplier.WorldSupplier;
import dev.lazurite.rayon.core.impl.physics.util.thread.Pausable;
import dev.lazurite.rayon.core.impl.physics.util.thread.ThreadStorage;
import dev.lazurite.rayon.core.impl.util.RayonException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.thread.ReentrantThreadExecutor;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;

/**
 * In order to access an instance of this, all you need is a {@link World} or {@link ReentrantThreadExecutor} object.
 * Calling {@link PhysicsThread#execute} adds a runnable to the queue of tasks and is the main way to execute code on
 * this thread. You can also execute code here by using {@link PhysicsSpaceEvents} as well as {@link PhysicsElement#step}.
 * @see PhysicsSpaceEvents
 * @see PhysicsElement
 * @see MinecraftSpace
 */
public class PhysicsThread extends Thread implements Executor, Pausable {
    private final Queue<Runnable> tasks = new ConcurrentLinkedQueue<>();
    private final Executor executor;
    private final WorldSupplier worldSupplier;
    public volatile boolean running = true;

    public static PhysicsThread get(ReentrantThreadExecutor<? extends Runnable> executor) {
        return ((ThreadStorage) executor).getPhysicsThread();
    }

    public static PhysicsThread get(World world) {
        return MinecraftSpace.get(world).getThread();
    }

    public PhysicsThread(Executor executor, WorldSupplier worldSupplier, String name) {
        this.executor = executor;
        this.worldSupplier = worldSupplier;

        this.setName(name);
        this.setUncaughtExceptionHandler((thread, throwable) -> {
            this.running = false;
            getThreadExecutor().execute(() -> {
                throw new RayonException("Uncaught exception on " + getName() + ": " + throwable + ".", throwable);
            });
        });

        RayonCoreCommon.LOGGER.info("Starting " + getName());
        this.start();
    }

    /**
     * The worker loop. Waits for tasks
     * and executes right away.
     */
    @Override
    public void run() {
        while (running) {
            while (!tasks.isEmpty()) {
                tasks.poll().run();
            }
        }
    }

    /**
     * For queueing up tasks to be executed on this thread. A {@link MinecraftSpace}
     * object is provided within the consumer.
     * @param task the task to run
     */
    public void execute(@NotNull Runnable task) {
        tasks.add(task);
    }

    /**
     * Gets the {@link WorldSupplier}. For servers, it is able to provide multiple worlds.
     * For clients, it will only provide one unless immersive portals is installed.
     * @return the {@link WorldSupplier}
     */
    public WorldSupplier getWorldSupplier() {
        return this.worldSupplier;
    }

    /**
     * Gets the original executor. Useful for returning to the main thread,
     * especially server-side where {@link MinecraftServer} isn't always readily
     * available.
     * @return the original {@link Executor}
     */
    public Executor getThreadExecutor() {
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
