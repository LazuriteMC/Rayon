package dev.lazurite.rayon.core.impl.event.fabric;

import dev.lazurite.rayon.core.api.event.collision.PhysicsSpaceEvents;
import dev.lazurite.rayon.core.impl.bullet.collision.space.generator.PressureGenerator;
import dev.lazurite.rayon.core.impl.event.ServerEventHandler;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;

public class ServerEventHandlerImpl {
    public static void register() {
        // Server Events
        ServerLifecycleEvents.SERVER_STARTING.register(ServerEventHandler::onServerStart);
        ServerLifecycleEvents.SERVER_STOPPED.register(ServerEventHandler::onServerStop);
        ServerTickEvents.END_SERVER_TICK.register(server -> ServerEventHandler.onServerTick());

        // World Events
        ServerTickEvents.START_WORLD_TICK.register(ServerEventHandler::onStartLevelTick);
        ServerWorldEvents.LOAD.register((server, level) -> ServerEventHandler.onLevelLoad(level));

        // Space Events
        PhysicsSpaceEvents.STEP.register(PressureGenerator::step);
    }
}
