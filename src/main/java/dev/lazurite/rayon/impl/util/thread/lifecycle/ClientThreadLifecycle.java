package dev.lazurite.rayon.impl.util.thread.lifecycle;

import dev.lazurite.rayon.api.element.PhysicsElement;
import dev.lazurite.rayon.impl.bullet.thread.PhysicsThread;
import dev.lazurite.rayon.impl.bullet.space.MinecraftSpace;
import dev.lazurite.rayon.impl.mixin.client.world.ClientWorldMixin;
import dev.lazurite.rayon.impl.util.space.SpaceStorage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

import java.util.concurrent.atomic.AtomicReference;

/**
 * All lifecycle events associated with the client thread.
 * Includes:
 * <ul>
 *     <li>Thread Spawning</li>
 *     <li>Thread Destruction</li>
 *     <li>Thread Ticking</li>
 *     <li>Space Creation</li>
 *     <li>Space Destruction*</li>
 *     <li>Rigid Body Loading</li>
 *     <li>Rigid Body Unloading</li>
 * </ul>
 * @see ClientWorldMixin
 */
@Environment(EnvType.CLIENT)
public class ClientThreadLifecycle {
    public static void register() {
        AtomicReference<PhysicsThread> thread = new AtomicReference<>();

        ClientLifecycleEvents.CLIENT_STARTED.register(client -> thread.set(new PhysicsThread(client, "Client Physics Thread")));
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> thread.get().destroy());
        ClientTickEvents.END_WORLD_TICK.register(client -> thread.get().tick());

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) ->
                ((SpaceStorage) client.world).setSpace(thread.get().createSpace(client.world)));

        ClientEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof PhysicsElement) {
                MinecraftSpace space = MinecraftSpace.get(world);
                space.getThread().execute(() -> space.load((PhysicsElement) entity));
            }
        });

        ClientEntityEvents.ENTITY_UNLOAD.register((entity, world) -> {
            if (entity instanceof PhysicsElement) {
                MinecraftSpace space = MinecraftSpace.get(world);
                space.getThread().execute(() -> space.unload((PhysicsElement) entity));
            }
        });
    }
}
