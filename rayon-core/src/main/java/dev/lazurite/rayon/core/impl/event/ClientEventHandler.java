package dev.lazurite.rayon.core.impl.event;

import dev.lazurite.rayon.core.api.event.collision.PhysicsSpaceEvents;
import dev.lazurite.rayon.core.impl.RayonCore;
import dev.lazurite.rayon.core.impl.bullet.thread.PhysicsThread;
import dev.lazurite.rayon.core.impl.bullet.collision.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.bullet.collision.space.storage.SpaceStorage;
import dev.lazurite.rayon.core.impl.bullet.collision.space.supplier.world.ClientWorldSupplier;
import dev.lazurite.rayon.core.impl.bullet.collision.space.supplier.world.compat.ImmersiveWorldSupplier;
import dev.lazurite.toolbox.api.event.BetterClientLifecycleEvents;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;

@Environment(EnvType.CLIENT)
public final class ClientEventHandler {
    private static PhysicsThread thread;

    public static PhysicsThread getThread() {
        return thread;
    }

    public static void register() {
        // Client Events
        BetterClientLifecycleEvents.GAME_JOIN.register(ClientEventHandler::onGameJoin);
        BetterClientLifecycleEvents.DISCONNECT.register(ClientEventHandler::onDisconnect);
        ClientTickEvents.END_CLIENT_TICK.register(ClientEventHandler::onClientTick);

        // World Events
        BetterClientLifecycleEvents.LOAD_WORLD.register(ClientEventHandler::onWorldLoad);
        ClientTickEvents.START_WORLD_TICK.register(ClientEventHandler::onWorldTick);
    }

    private static void onWorldTick(ClientWorld world) {
        var space = MinecraftSpace.get(world);
        space.step(space::canStep);
    }

    private static void onWorldLoad(MinecraftClient client, ClientWorld world) {
        var space = new MinecraftSpace(thread, world);
        ((SpaceStorage) world).setSpace(space);
        PhysicsSpaceEvents.INIT.invoker().onInit(space);
    }

    private static void onClientTick(MinecraftClient client) {
        if (thread != null && thread.throwable != null) {
            throw new RuntimeException(thread.throwable);
        }
    }

    private static void onGameJoin(MinecraftClient client, ClientWorld world, ClientPlayerEntity player) {
        var supplier = RayonCore.isImmersivePortalsPresent() ? new ImmersiveWorldSupplier(client) : new ClientWorldSupplier(client);
        thread = new PhysicsThread(client, Thread.currentThread(), supplier, "Client Physics Thread");
    }

    private static void onDisconnect(MinecraftClient client, ClientWorld world) {
        thread.destroy();
    }
}
