package dev.lazurite.rayon.core.impl.util.lifecycle;

import dev.lazurite.rayon.core.api.event.PhysicsSpaceEvents;
import dev.lazurite.rayon.core.impl.bullet.thread.PhysicsThread;
import dev.lazurite.rayon.core.impl.bullet.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.bullet.space.storage.SpaceStorage;
import dev.lazurite.rayon.core.impl.bullet.space.supplier.world.ServerWorldSupplier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;

public final class ServerLifecycleHandler {
    private static PhysicsThread thread;

    public static void onWorldTick(ServerWorld world) {
        var space = MinecraftSpace.get(world);
        space.step(space::canStep);
    }

    public static void onWorldLoad(MinecraftServer server, ServerWorld world) {
        var space = new MinecraftSpace(thread, world);
        ((SpaceStorage) world).setSpace(space);
        PhysicsSpaceEvents.INIT.invoker().onInit(space);
    }

    public static void onServerTick(MinecraftServer server) {
        if (thread.throwable != null) {
            throw new RuntimeException(thread.throwable);
        }
    }

    public static void onServerStart(MinecraftServer server) {
        thread = new PhysicsThread(server, Thread.currentThread(), new ServerWorldSupplier(server), "Server Physics Thread");
    }

    public static void onServerStop(MinecraftServer server) {
        thread.destroy();
    }

    public static PhysicsThread getThread() {
        return thread;
    }
}
