package dev.lazurite.rayon.impl.bullet.thread;

import dev.lazurite.rayon.impl.bullet.space.MinecraftSpace;
import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Util;
import net.minecraft.world.World;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

public class PhysicsThread extends Thread implements ComponentV3 {
    private static final long STEP_SIZE = 15L;
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
