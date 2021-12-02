package dev.lazurite.rayon.impl.event.fabric;

import dev.lazurite.rayon.api.event.collision.PhysicsSpaceEvents;
import dev.lazurite.rayon.impl.bullet.collision.space.generator.PressureGenerator;
import dev.lazurite.rayon.impl.event.ServerEventHandler;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;

public class ServerEventHandlerImpl {
    public static void register() {
        // Server Events
        ServerLifecycleEvents.SERVER_STARTING.register(ServerEventHandler::onServerStart);
        ServerLifecycleEvents.SERVER_STOPPED.register(ServerEventHandler::onServerStop);
        ServerTickEvents.END_SERVER_TICK.register(server -> ServerEventHandler.onServerTick());

        // Level Events
        ServerWorldEvents.LOAD.register((server, level) -> ServerEventHandler.onLevelLoad(level));
        ServerTickEvents.START_WORLD_TICK.register(ServerEventHandler::onStartLevelTick);
        ServerTickEvents.START_WORLD_TICK.register(ServerEventHandler::onEntityStartLevelTick);
        PhysicsSpaceEvents.STEP.register(PressureGenerator::step);

        // Entity Events
        PhysicsSpaceEvents.ELEMENT_ADDED.register(ServerEventHandler::onElementAddedToSpace);
        ServerEntityEvents.ENTITY_LOAD.register(ServerEventHandler::onEntityLoad);
        EntityTrackingEvents.START_TRACKING.register(ServerEventHandler::onStartTrackingEntity);
        EntityTrackingEvents.STOP_TRACKING.register(ServerEventHandler::onStopTrackingEntity);
    }
}