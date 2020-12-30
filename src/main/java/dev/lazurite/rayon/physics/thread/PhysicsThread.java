package dev.lazurite.rayon.physics.thread;

import dev.lazurite.rayon.physics.entity.RigidBodyEntity;
import dev.lazurite.rayon.physics.world.MinecraftDynamicsWorld;
import net.minecraft.entity.Entity;

import java.util.Timer;
import java.util.TimerTask;

public class PhysicsThread extends TimerTask {
    private final MinecraftDynamicsWorld world;
    private final Delta clock;
    private final Timer timer;

    public PhysicsThread(MinecraftDynamicsWorld world, int rate) {
        this.world = world;
        this.clock = new Delta();
        this.timer = new Timer();

        /*
         * 4 = 250 fps
         * 10 = 100 fps
         * 50 = 20 fps (minecraft ticks)
         */
        timer.scheduleAtFixedRate(this, 0, 1000 / rate);
    }

    @Override
    public void run() {
        float delta = clock.get();

        /* Step the server world */
        world.step(delta);

        /* Step every entity */
        for (Entity entity : world.getEntities()) {
            RigidBodyEntity.get(entity).step(delta);
        }
    }

    public void stop() {
        timer.cancel();
    }

    public float getDelta() {
        return clock.getTimeMicroseconds() / 1000000F;
    }

    public MinecraftDynamicsWorld getWorld() {
        return this.world;
    }
}