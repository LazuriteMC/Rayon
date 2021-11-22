package dev.lazurite.rayon.core.impl.event.forge;

import dev.lazurite.rayon.core.api.event.collision.PhysicsSpaceEvents;
import dev.lazurite.rayon.core.impl.bullet.collision.space.generator.PressureGenerator;
import dev.lazurite.rayon.core.impl.event.ServerEventHandler;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fmlserverevents.FMLServerAboutToStartEvent;
import net.minecraftforge.fmlserverevents.FMLServerStoppedEvent;

public class ServerEventHandlerImpl {
    public static void register() {
        // Space Events
        PhysicsSpaceEvents.STEP.register(PressureGenerator::step);
    }

    @SubscribeEvent
    public static void onServerStart(FMLServerAboutToStartEvent event) {
        ServerEventHandler.onServerStart(event.getServer());
    }

    @SubscribeEvent
    public static void onServerStop(FMLServerStoppedEvent event) {
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
        }
    }

    @SubscribeEvent
    public static void onLevelLoad(WorldEvent.Load event) {
        if (event.getWorld() instanceof ServerLevel) {
            ServerEventHandler.onLevelLoad((Level) event.getWorld());
        }
    }
}