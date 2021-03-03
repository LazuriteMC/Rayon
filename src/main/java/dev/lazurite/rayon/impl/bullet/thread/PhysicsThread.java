package dev.lazurite.rayon.impl.bullet.thread;

import dev.lazurite.rayon.api.element.PhysicsElement;
import dev.lazurite.rayon.api.event.PhysicsSpaceEvents;
import dev.lazurite.rayon.impl.Rayon;
import dev.lazurite.rayon.impl.bullet.world.MinecraftSpace;
import dev.lazurite.rayon.impl.util.RayonException;
import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Util;
import net.minecraft.world.World;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

/**
 * In order to access an instance of this, all you need is a {@link World} object. The main way to execute
 * tasks on the physics thread is by called {@link PhysicsThread#execute} which gives you access to the
 * {@link MinecraftSpace} object. There are several other ways to execute on the physics thread including
 * registering an event callback in {@link PhysicsSpaceEvents} or inserting code into your
 * {@link PhysicsElement#step} method. It is also possible to retrieve the {@link MinecraftSpace} without
 * switching to the physics thread using {@link PhysicsThread#getSpace()} but this is generally discouraged
 * since thread safety becomes an issue if you attempt to modify data within it.
 * @see PhysicsSpaceEvents
 * @see MinecraftSpace
 */
public class PhysicsThread extends Thread implements ComponentV3, CommonTickingComponent {
    public static final float STEP_SIZE = 1f / 60f; // in seconds

    private final Queue<Consumer<MinecraftSpace>> tasks = new ConcurrentLinkedQueue<>();
    private MinecraftSpace space;
    private final World world;
    private long nextStep;
    private Throwable throwable;
    private boolean running = true;

    public PhysicsThread(World world) {
        this.world = world;
        this.nextStep = Util.getMeasuringTimeMs() + (long) (STEP_SIZE * 1000);
        this.setName(world.isClient() ? "Client Physics Thread" : "Server Physics Thread - " + world.getRegistryKey().getValue());
        this.setUncaughtExceptionHandler((thread, throwable) -> this.throwable = throwable);
        this.start();
    }

    /**
     * This checks for any uncaught exception on the physics thread. This
     * allows the error to be returned to the main thread and the game will
     * crash in the usual way.
     */
    @Override
    public void tick() {
        if (throwable != null) {
            throw new RayonException(
                    "Uncaught exception on " + getName() + ": " + throwable + ".",
                    throwable);
        }
    }

    @Override
    public void run() {
        /* Create and load the physics space */
        this.space = new MinecraftSpace(this, world);
        this.space.setAccuracy(STEP_SIZE);
        PhysicsSpaceEvents.LOAD.invoker().onLoad(space);

        /* Loop while it is still supposed to be running */
        while (running) {
            if (Util.getMeasuringTimeMs() > nextStep) {
                nextStep = Util.getMeasuringTimeMs() + (long) (STEP_SIZE * 1000);

                /* Run all queued tasks */
                while (!tasks.isEmpty()) {
                    tasks.poll().accept(space);
                }

                /* Step the physics space */
                space.step();
            }
        }
    }

    /**
     * For queueing up tasks to be executed on this thread. A {@link MinecraftSpace}
     * object is provided within the consumer.
     * @param task the task to run
     */
    public void execute(Consumer<MinecraftSpace> task) {
        tasks.add(task);
    }

    /**
     * @return whether or not the thread is running
     */
    public boolean isRunning() {
        return running;
    }

    public void destroy() {
        this.running = false;

        try {
            this.join();
        } catch (InterruptedException e) {
            Rayon.LOGGER.error("Error joining " + getName());
            e.printStackTrace();
        }
    }

    /**
     * Gets a reference to the {@link MinecraftSpace}. Only use this if you're
     * going to read data from it or perform a thread safe operation. Else, you
     * should use {@link PhysicsThread#execute} instead.
     * @return the {@link MinecraftSpace} for this thread
     */
    public MinecraftSpace getSpace() {
        return this.space;
    }

    @Override
    public void readFromNbt(CompoundTag compoundTag) { }

    @Override
    public void writeToNbt(CompoundTag compoundTag) { }
}
