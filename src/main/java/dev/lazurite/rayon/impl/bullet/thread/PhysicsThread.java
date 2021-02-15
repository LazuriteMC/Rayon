package dev.lazurite.rayon.impl.bullet.thread;

import dev.lazurite.rayon.api.element.PhysicsElement;
import dev.lazurite.rayon.api.event.PhysicsSpaceEvents;
import dev.onyxstudios.cca.api.v3.component.ComponentV3;
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
public class PhysicsThread extends Thread implements ComponentV3 {
    private static final long STEP_SIZE = 20L;
    private static int serverThreads;

    private final Queue<Consumer<MinecraftSpace>> tasks = new ConcurrentLinkedQueue<>();
    private final World world;
    private MinecraftSpace space;
    private long nextStep;

    public PhysicsThread(World world) {
        this.world = world;
        this.nextStep = Util.getMeasuringTimeMs() + STEP_SIZE;

        if (world.isClient()) {
            this.setName("Client Physics Thread");
        } else {
            ++serverThreads;
            this.setName("Server Physics Thread " + serverThreads);
        }

        this.start();
    }

    @Override
    public void run() {
        this.space = new MinecraftSpace(this, world);
        this.space.setAccuracy(STEP_SIZE * 0.001f);
        PhysicsSpaceEvents.LOAD.invoker().onLoad(space);

        while (!space.isDestroyed()) {
            if (Util.getMeasuringTimeMs() > nextStep) {
                nextStep = Util.getMeasuringTimeMs() + STEP_SIZE;

                while (!tasks.isEmpty()) {
                    tasks.poll().accept(space);
                }

                space.step();
            }
        }
    }

    public void execute(Consumer<MinecraftSpace> consumer) {
        tasks.add(consumer);
    }

    public MinecraftSpace getSpace() {
        return this.space;
    }

    @Override
    public void readFromNbt(CompoundTag compoundTag) {

    }

    @Override
    public void writeToNbt(CompoundTag compoundTag) {

    }
}
