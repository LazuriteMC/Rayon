package dev.lazurite.rayon.impl.event.fabric;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.lazurite.rayon.impl.event.ClientEventHandler;
import dev.lazurite.toolbox.api.event.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

public class ClientEventHandlerImpl {
    public static void register() {
        // Client Events
        ClientLifecycleEvents.LOGIN.register(ClientEventHandler::onGameJoin);
        ClientLifecycleEvents.DISCONNECT.register(ClientEventHandler::onDisconnect);
        ClientTickEvents.END_CLIENT_TICK.register(ClientEventHandler::onClientTick);

        // Level Events
        ClientLifecycleEvents.LOAD_LEVEL.register(ClientEventHandler::onLevelLoad);
        ClientTickEvents.END_WORLD_TICK.register(ClientEventHandler::onStartLevelTick);
        ClientTickEvents.END_WORLD_TICK.register(ClientEventHandler::onEntityStartLevelTick);

        // Render Events
        WorldRenderEvents.BEFORE_DEBUG_RENDER.register(context -> ClientEventHandler.onDebugRender(context.world(), new PoseStack(), context.tickDelta()));

        // Entity Events
        ClientEntityEvents.ENTITY_LOAD.register(ClientEventHandler::onEntityLoad);
        ClientEntityEvents.ENTITY_UNLOAD.register(ClientEventHandler::onEntityUnload);
    }
}