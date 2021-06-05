package dev.lazurite.rayon.core.impl;

import dev.lazurite.rayon.core.impl.bullet.thread.PhysicsThread;
import dev.lazurite.rayon.core.impl.client.CollisionObjectDebugger;
import dev.lazurite.rayon.core.impl.bullet.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.client.event.BetterClientLifecycleEvents;
import dev.lazurite.rayon.core.impl.util.lifecycle.ClientLifecycleHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

/**
 * The client entrypoint for Rayon Core. Handles the lifecycle of the physics
 * thread as well as the creation of {@link MinecraftSpace}s.
 * @see RayonCore
 */
@Environment(EnvType.CLIENT)
public class RayonCoreClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Client Events
        BetterClientLifecycleEvents.GAME_JOIN.register(ClientLifecycleHandler::onGameJoin);
        BetterClientLifecycleEvents.DISCONNECT.register(ClientLifecycleHandler::onDisconnect);
        ClientTickEvents.END_CLIENT_TICK.register(ClientLifecycleHandler::onClientTick);

        // World Events
        BetterClientLifecycleEvents.LOAD_WORLD.register(ClientLifecycleHandler::onWorldLoad);
        ClientTickEvents.START_WORLD_TICK.register(ClientLifecycleHandler::onWorldTick);

        // Debug Events
        WorldRenderEvents.BEFORE_DEBUG_RENDER.register(context -> {
            if (CollisionObjectDebugger.getInstance().isEnabled()) {
                CollisionObjectDebugger.getInstance().renderSpace(MinecraftSpace.get(context.world()), context.tickDelta());
            }
        });
    }

    public static PhysicsThread getThread() {
        return ClientLifecycleHandler.getThread();
    }
}
