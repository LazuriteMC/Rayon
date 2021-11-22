package dev.lazurite.rayon.core.impl.event.fabric;

import dev.lazurite.rayon.core.impl.event.ClientEventHandler;
import dev.lazurite.toolbox.api.event.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

public class ClientEventHandlerImpl {
    public static void register() {
        // Client Events
        ClientLifecycleEvents.LOGIN.register(ClientEventHandler::onGameJoin);
        ClientLifecycleEvents.DISCONNECT.register(ClientEventHandler::onDisconnect);
        ClientTickEvents.END_CLIENT_TICK.register(ClientEventHandler::onClientTick);

        // World Events
        ClientLifecycleEvents.LOAD_LEVEL.register(ClientEventHandler::onLevelLoad);
        ClientTickEvents.START_WORLD_TICK.register(ClientEventHandler::onStartLevelTick);

        // Render Events
        WorldRenderEvents.BEFORE_DEBUG_RENDER.register((context) -> ClientEventHandler.onDebugRender(context.world(), context.tickDelta()));
    }
}