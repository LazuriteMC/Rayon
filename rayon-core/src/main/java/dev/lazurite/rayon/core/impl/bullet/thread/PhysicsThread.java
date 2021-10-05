package dev.lazurite.rayon.core.impl.bullet.thread;

import dev.lazurite.rayon.core.api.PhysicsElement;
import dev.lazurite.rayon.core.api.event.collision.PhysicsSpaceEvents;
import dev.lazurite.rayon.core.impl.RayonCore;
import dev.lazurite.rayon.core.impl.bullet.collision.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.bullet.collision.space.supplier.entity.EntitySupplier;
import dev.lazurite.rayon.core.impl.bullet.collision.space.supplier.level.LevelSupplier;
import dev.lazurite.rayon.core.impl.bullet.thread.util.Pausable;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;

/**
 * In order to access an instance of this, all you need is a {@link Level} or {@link ReentrantBlockableEventLoop} object.
 * Calling {@link PhysicsThread#execute} adds a runnable to the queue of tasks and is the main way to execute code on
 * this thread. You can also execute code here by using {@link PhysicsSpaceEvents}.
 * @see PhysicsSpaceEvents
 * @see PhysicsElement
 * @see MinecraftSpace
 */
public class PhysicsThread extends Thread implements Executor, Pausable {
    private final Queue<Runnable> tasks = new ConcurrentLinkedQueue<>();
    private final Executor parentExecutor;
    private final Thread parentThread;
    private final LevelSupplier levelSupplier;

    public volatile Throwable throwable;
    public volatile boolean running = true;

    public static Optional<PhysicsThread> getOptional(ReentrantBlockableEventLoop<? extends Runnable> executor) {
        return Optional.ofNullable(get(executor));
    }

    public static PhysicsThread get(ReentrantBlockableEventLoop<? extends Runnable> executor) {
        return RayonCore.getThread(!(executor instanceof MinecraftServer));
    }

    public static PhysicsThread get(Level level) {
        return MinecraftSpace.get(level).getWorkerThread();
    }

    public PhysicsThread(Executor parentExecutor, Thread parentThread, LevelSupplier levelSupplier, String name) {
        this.parentExecutor = parentExecutor;
        this.parentThread = parentThread;
        this.levelSupplier = levelSupplier;

        this.setName(name);
        this.setUncaughtExceptionHandler((thread, throwable) -> {
            this.running = false;
            this.throwable = throwable;
        });

        RayonCore.LOGGER.info("Starting " + getName());
        this.start();
    }

    /**
     * The worker loop. Waits for tasks and executes right away.
     */
    @Override
    public void run() {
        while (running) {
            if (!isPaused()) {
                /* Run all queued tasks */
                while (!tasks.isEmpty()) {
                    tasks.poll().run();
                }
            }
        }
    }

    /**
     * For queueing up tasks to be executed on this thread. A {@link MinecraftSpace}
     * object is provided within the consumer.
     * @param task the task to run
     */
    @Override
    public void execute(@NotNull Runnable task) {
        tasks.add(task);
    }

    /**
     * Gets the {@link LevelSupplier}. For servers, it is able to provide multiple worlds.
     * For clients, it will only provide one unless immersive portals is installed.
     * @return the {@link LevelSupplier}
     */
    public LevelSupplier getLevelSupplier() {
        return this.levelSupplier;
    }

    /**
     * Gets the parent executor. Useful for returning to the main thread,
     * especially server-side where {@link MinecraftServer} isn't always readily
     * available.
     * @return the original {@link Executor} object
     */
    public Executor getParentExecutor() {
        return this.parentExecutor;
    }

    /**
     * Gets the parent thread. This is useful for checking
     * whether a method is executing on this thread.
     * @see EntitySupplier
     * @return the parent {@link Thread} object
     */
    public Thread getParentThread() {
        return this.parentThread;
    }

    /**
     * Join the thread when the game closes.
     */
    public void destroy() {
        this.running = false;
        RayonCore.LOGGER.info("Stopping " + getName());

        try {
            this.join();
        } catch (InterruptedException e) {
            RayonCore.LOGGER.error("Error joining " + getName());
            e.printStackTrace();
        }
    }
}
