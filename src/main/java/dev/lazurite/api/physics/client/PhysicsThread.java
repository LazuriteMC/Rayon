package dev.lazurite.api.physics.client;

import com.bulletphysics.linearmath.Clock;
import net.minecraft.client.MinecraftClient;

public class PhysicsThread extends Thread {
    private final MinecraftClient client;
    private final PhysicsWorld world;
    private final Clock clock;

    private int tickRate;
    private float timePassed;

    public PhysicsThread(PhysicsWorld world, MinecraftClient client, int tickRate) {
        this.world = world;
        this.client = client;
        this.tickRate = tickRate;
        this.clock = new Clock();
    }

    @Override
    public void run() {
        while (!world.isDestroyed()) {
            float delta = clock.getTimeMicroseconds() / 1000000F;
            timePassed += delta;
            clock.reset();

            if (!client.isPaused() && timePassed > getTimeStep()) {
                timePassed -= getTimeStep();
                world.stepWorld(delta);
            }
        }
    }

    private float getTimeStep() {
        return 1.0f / tickRate;
    }

    public void setTickRate(int tickRate) {
        this.tickRate = tickRate;
    }
}
