package dev.lazurite.rayon.core.impl.event;

import dev.lazurite.rayon.core.api.event.collision.PhysicsSpaceEvents;
import dev.lazurite.rayon.core.impl.bullet.collision.space.generator.PressureGenerator;
import dev.lazurite.rayon.core.impl.bullet.thread.PhysicsThread;
import dev.lazurite.rayon.core.impl.bullet.collision.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.bullet.collision.space.storage.SpaceStorage;
import dev.lazurite.rayon.core.impl.bullet.collision.space.supplier.level.ServerLevelSupplier;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

public final class ServerEventHandler {
    private static PhysicsThread thread;

    public static PhysicsThread getThread() {
        return thread;
    }

    public static void register() {
        // Server Events
        ServerLifecycleEvents.SERVER_STARTING.register(ServerEventHandler::onServerStart);
        ServerLifecycleEvents.SERVER_STOPPING.register(ServerEventHandler::onServerStop);
        ServerTickEvents.END_SERVER_TICK.register(ServerEventHandler::onServerTick);

        // World Events
        ServerTickEvents.START_WORLD_TICK.register(ServerEventHandler::onStartLevelTick);
        ServerWorldEvents.LOAD.register(ServerEventHandler::onLevelLoad);

        // Space Events
        PhysicsSpaceEvents.STEP.register(PressureGenerator::step);
    }

    private static void onServerStart(MinecraftServer server) {
        thread = new PhysicsThread(server, Thread.currentThread(), new ServerLevelSupplier(server), "Server Physics Thread");
    }

    private static void onServerStop(MinecraftServer server) {
        thread.destroy();
    }

    private static void onServerTick(MinecraftServer server) {
        if (thread.throwable != null) {
            throw new RuntimeException(thread.throwable);
        }
    }

    private static void onStartLevelTick(ServerLevel level) {
        final var space = MinecraftSpace.get(level);

        if (!space.getWorkerThread().isPaused()) {
            space.step();
        }
    }

    private static void onLevelLoad(MinecraftServer server, ServerLevel level) {
        final var space = new MinecraftSpace(thread, level);
        ((SpaceStorage) level).setSpace(space);
        PhysicsSpaceEvents.INIT.invoker().onInit(space);
    }
}