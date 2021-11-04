package dev.lazurite.rayon.core.common.impl.event;

import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.TickEvent;
import dev.lazurite.rayon.core.common.api.event.collision.PhysicsSpaceEvents;
import dev.lazurite.rayon.core.common.impl.bullet.collision.space.storage.SpaceStorage;
import dev.lazurite.rayon.core.common.impl.bullet.collision.space.supplier.level.ServerLevelSupplier;
import dev.lazurite.rayon.core.common.impl.bullet.thread.PhysicsThread;
import dev.lazurite.rayon.core.common.impl.bullet.collision.space.generator.PressureGenerator;
import dev.lazurite.rayon.core.common.impl.bullet.collision.space.MinecraftSpace;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

public final class ServerEventHandler {
    private static PhysicsThread thread;

    public static PhysicsThread getThread() {
        return thread;
    }

    public static void register() {
        // Server Events
        LifecycleEvent.SERVER_STARTING.register(ServerEventHandler::onServerStart);
        LifecycleEvent.SERVER_STOPPING.register(ServerEventHandler::onServerStop);
        TickEvent.SERVER_POST.register(ServerEventHandler::onServerTick);

        // World Events
        TickEvent.SERVER_LEVEL_POST.register(ServerEventHandler::onStartLevelTick);
        LifecycleEvent.SERVER_LEVEL_LOAD.register(ServerEventHandler::onLevelLoad);

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

    private static void onLevelLoad(ServerLevel level) {
        final var space = new MinecraftSpace(thread, level);
        ((SpaceStorage) level).setSpace(space);
        PhysicsSpaceEvents.INIT.invoker().onInit(space);
    }
}