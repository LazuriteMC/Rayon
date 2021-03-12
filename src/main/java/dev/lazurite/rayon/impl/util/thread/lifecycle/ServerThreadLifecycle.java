package dev.lazurite.rayon.impl.util.thread.lifecycle;

import dev.lazurite.rayon.api.element.PhysicsElement;
import dev.lazurite.rayon.impl.bullet.thread.PhysicsThread;
import dev.lazurite.rayon.impl.bullet.space.MinecraftSpace;
import dev.lazurite.rayon.impl.util.space.SpaceStorage;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;

import java.util.concurrent.atomic.AtomicReference;

/**
 * All lifecycle events associated with the client thread.
 * Includes:
 * <ul>
 *     <li>Thread Spawning</li>
 *     <li>Thread Destruction</li>
 *     <li>Thread Ticking</li>
 *     <li>Space Creation</li>
 *     <li>Rigid Body Loading</li>
 *     <li>Rigid Body Tracking (Start/Stop)</li>
 * </ul>
 */
public class ServerThreadLifecycle {
    public static void register() {
        AtomicReference<PhysicsThread> thread = new AtomicReference<>();

        ServerLifecycleEvents.SERVER_STARTING.register(server -> thread.set(new PhysicsThread(server, "Server Physics Thread")));
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> thread.get().destroy());
        ServerTickEvents.END_SERVER_TICK.register(server -> thread.get().tick());

        ServerWorldEvents.LOAD.register((server, world) ->
                ((SpaceStorage) world).setSpace(thread.get().createSpace(world)));

        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof PhysicsElement && !PlayerLookup.tracking(entity).isEmpty()) {
                MinecraftSpace space = MinecraftSpace.get(entity.getEntityWorld());
                space.getThread().execute(() -> space.load((PhysicsElement) entity));
            }
        });

        EntityTrackingEvents.START_TRACKING.register((entity, player) -> {
            if (entity instanceof PhysicsElement) {
                MinecraftSpace space = MinecraftSpace.get(entity.getEntityWorld());
                space.getThread().execute(() -> space.load((PhysicsElement) entity));
            }
        });

        EntityTrackingEvents.STOP_TRACKING.register((entity, player) -> {
            if (entity instanceof PhysicsElement && PlayerLookup.tracking(entity).isEmpty()) {
                MinecraftSpace space = MinecraftSpace.get(entity.getEntityWorld());
                space.getThread().execute(() -> space.unload((PhysicsElement) entity));
            }
        });
    }
}
