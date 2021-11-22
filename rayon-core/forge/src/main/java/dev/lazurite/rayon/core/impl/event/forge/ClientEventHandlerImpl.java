package dev.lazurite.rayon.core.impl.event.forge;

import dev.lazurite.rayon.core.impl.event.ClientEventHandler;
import dev.lazurite.toolbox.api.event.ClientLifecycleEvents;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

public class ClientEventHandlerImpl {
    public static void register() {
        // Client Events
        ClientLifecycleEvents.LOGIN.register(ClientEventHandler::onGameJoin);
        ClientLifecycleEvents.DISCONNECT.register(ClientEventHandler::onDisconnect);

        // World Events
        ClientLifecycleEvents.LOAD_LEVEL.register(ClientEventHandler::onLevelLoad);
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            ClientEventHandler.onClientTick(Minecraft.getInstance());
        }
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.side == LogicalSide.CLIENT && event.phase == TickEvent.Phase.START) {
            ClientEventHandler.onStartLevelTick(event.world);
        }
    }

    @SubscribeEvent
    public static void onDebugRender(RenderWorldLastEvent event) {
        ClientEventHandler.onDebugRender(Minecraft.getInstance().level, event.getPartialTicks());
    }
}