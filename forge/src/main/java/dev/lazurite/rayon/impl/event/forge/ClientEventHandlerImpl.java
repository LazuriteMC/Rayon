package dev.lazurite.rayon.impl.event.forge;

import dev.lazurite.rayon.impl.event.ClientEventHandler;
import dev.lazurite.toolbox.api.event.ClientLifecycleEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityLeaveWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

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
            final var level = Minecraft.getInstance().level;

            if (level != null) {
                ClientEventHandler.onStartLevelTick(level);
                ClientEventHandler.onEntityStartLevelTick(level);
            }
        }
    }

    @SubscribeEvent
    public static void onDebugRender(RenderLevelLastEvent event) {
        ClientEventHandler.onDebugRender(Minecraft.getInstance().level, event.getPoseStack(), event.getPartialTick());
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