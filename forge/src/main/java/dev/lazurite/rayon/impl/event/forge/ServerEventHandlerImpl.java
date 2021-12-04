package dev.lazurite.rayon.impl.event.forge;

import dev.lazurite.rayon.api.event.collision.PhysicsSpaceEvents;
import dev.lazurite.rayon.impl.bullet.collision.space.generator.PressureGenerator;
import dev.lazurite.rayon.impl.event.ServerEventHandler;
import dev.lazurite.toolbox.api.util.PlayerUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

public class ServerEventHandlerImpl {
    public static void register() {
        // Space Events
        PhysicsSpaceEvents.STEP.register(PressureGenerator::step);

        // Entity Events
        PhysicsSpaceEvents.ELEMENT_ADDED.register(ServerEventHandler::onElementAddedToSpace);
    }

    @SubscribeEvent
    public static void onServerStart(ServerAboutToStartEvent event) {
        ServerEventHandler.onServerStart(event.getServer());
    }

    @SubscribeEvent
    public static void onServerStop(ServerStoppedEvent event) {
        ServerEventHandler.onServerStop(event.getServer());
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            ServerEventHandler.onServerTick();
        }
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.side == LogicalSide.SERVER && event.phase == TickEvent.Phase.START) {
            ServerEventHandler.onStartLevelTick(event.world);
            ServerEventHandler.onEntityStartLevelTick(event.world);
        }
    }

    @SubscribeEvent
    public static void onLevelLoad(WorldEvent.Load event) {
        if (event.getWorld() instanceof ServerLevel) {
            ServerEventHandler.onLevelLoad((Level) event.getWorld());
        }
    }

    @SubscribeEvent
    public static void onEntityLoad(EntityJoinWorldEvent event) {
        if (event.getWorld() instanceof ServerLevel) {
            ServerEventHandler.onEntityLoad(event.getEntity(), event.getWorld());
        }
    }

    @SubscribeEvent
    public static void onStartTrackingEntity(PlayerEvent.StartTracking event) {
        final var players = PlayerUtil.tracking(event.getTarget());

        if (players.size() == 1) {
            ServerEventHandler.onStartTrackingEntity(event.getTarget(), (ServerPlayer) event.getPlayer());
        }
    }

    @SubscribeEvent
    public static void onStopTrackingEntity(PlayerEvent.StopTracking event) {
        final var players = PlayerUtil.tracking(event.getTarget());

        if (players.size() == 0) {
            ServerEventHandler.onStopTrackingEntity(event.getTarget(), (ServerPlayer) event.getPlayer());
        }
    }
}