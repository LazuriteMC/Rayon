package dev.lazurite.rayon.impl.event.forge;

import dev.lazurite.rayon.impl.event.ClientEventHandler;
import dev.lazurite.toolbox.api.event.ClientLifecycleEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityLeaveWorldEvent;
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
            ClientEventHandler.onEntityStartLevelTick(event.world);
        }
    }

    @SubscribeEvent
    public static void onDebugRender(RenderWorldLastEvent event) {
        ClientEventHandler.onDebugRender(Minecraft.getInstance().level, event.getPartialTicks());
    }

    @SubscribeEvent
    public static void onEntityLoad(EntityJoinWorldEvent event) {
        if (event.getWorld() instanceof ClientLevel) {
            ClientEventHandler.onEntityLoad(event.getEntity(), event.getWorld());
        }
    }

    @SubscribeEvent
    public static void onEntityUnload(EntityLeaveWorldEvent event) {
        if (event.getWorld() instanceof ClientLevel) {
            ClientEventHandler.onEntityUnload(event.getEntity(), event.getWorld());
        }
    }
}