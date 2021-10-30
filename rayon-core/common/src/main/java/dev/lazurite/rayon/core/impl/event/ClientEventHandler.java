package dev.lazurite.rayon.core.impl.event;

import dev.lazurite.rayon.core.api.event.collision.PhysicsSpaceEvents;
import dev.lazurite.rayon.core.impl.bullet.thread.PhysicsThread;
import dev.lazurite.rayon.core.impl.bullet.collision.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.bullet.collision.space.storage.SpaceStorage;
import dev.lazurite.rayon.core.impl.bullet.collision.space.supplier.level.ClientLevelSupplier;
import dev.lazurite.toolbox.api.event.BetterClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;

public final class ClientEventHandler {
    private static PhysicsThread thread;

    public static PhysicsThread getThread() {
        return thread;
    }

    public static void register() {
        // Client Events
        BetterClientLifecycleEvents.LOGIN.register(ClientEventHandler::onGameJoin);
        BetterClientLifecycleEvents.DISCONNECT.register(ClientEventHandler::onDisconnect);
        ClientTickEvents.END_CLIENT_TICK.register(ClientEventHandler::onClientTick);

        // World Events
        BetterClientLifecycleEvents.LOAD_LEVEL.register(ClientEventHandler::onLevelLoad);
        ClientTickEvents.START_WORLD_TICK.register(ClientEventHandler::onStartLevelTick);
    }

    private static void onStartLevelTick(ClientLevel level) {
        final var space = MinecraftSpace.get(level);

        if (!space.getWorkerThread().isPaused()) {
            space.step();
        }
    }

    private static void onLevelLoad(Minecraft minecraft, ClientLevel level) {
        final var space = new MinecraftSpace(thread, level);
        ((SpaceStorage) level).setSpace(space);
        PhysicsSpaceEvents.INIT.invoker().onInit(space);
    }

    private static void onClientTick(Minecraft minecraft) {
        if (thread != null && thread.throwable != null) {
            throw new RuntimeException(thread.throwable);
        }
    }

    private static void onGameJoin(Minecraft minecraft, ClientLevel level, LocalPlayer player) {
//        var supplier = RayonCore.isImmersivePortalsPresent() ? new ImmersiveWorldSupplier(minecraft) : new ClientLevelSupplier(minecraft);
        final var supplier = new ClientLevelSupplier(minecraft);
        thread = new PhysicsThread(minecraft, Thread.currentThread(), supplier, "Client Physics Thread");
    }

    private static void onDisconnect(Minecraft minecraft, ClientLevel level) {
        thread.destroy();
    }
}
