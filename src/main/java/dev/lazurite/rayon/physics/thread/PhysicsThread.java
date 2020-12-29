package dev.lazurite.rayon.physics.thread;

import dev.lazurite.rayon.physics.entity.DynamicPhysicsEntity;
import dev.lazurite.rayon.physics.world.MinecraftDynamicsWorld;
import net.minecraft.entity.Entity;

import java.util.Timer;
import java.util.TimerTask;

public class PhysicsThread extends TimerTask {
    private final MinecraftDynamicsWorld world;
    private final Delta clock;
    private final Timer timer;

    public PhysicsThread(MinecraftDynamicsWorld world) {
        this.world = world;
        this.clock = new Delta();
        this.timer = new Timer();
    }

    public void start() {
        /*
         * 4 = 250 fps
         * 10 = 100 fps
         * 50 = 20 fps (minecraft ticks)
         */
        timer.scheduleAtFixedRate(this, 0, 50);
    }

    @Override
    public void run() {
        float delta = clock.get();

        /* Step the server world */
        world.step(delta);

        /* Step every entity */
        for (Entity entity : world.getEntities()) {
            DynamicPhysicsEntity.get(entity).step(delta);
        }
    }

    public MinecraftDynamicsWorld getWorld() {
        return this.world;
    }
}