package dev.lazurite.rayon.core.impl;

import dev.lazurite.rayon.core.impl.bullet.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.bullet.thread.PhysicsThread;
import dev.lazurite.rayon.core.impl.util.NativeLoader;
import dev.lazurite.rayon.core.impl.util.space.SpaceStorage;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.atomic.AtomicReference;

/**
 * The main entrypoint for Rayon. Mainly contains packet
 * registrations and the loading of bullet natives.
 * @see NativeLoader
 */
public class RayonCore implements ModInitializer {
	public static final String MODID = "rayon-core";
	public static final Logger LOGGER = LogManager.getLogger("Rayon Core");

	@Override
	public void onInitialize() {
		NativeLoader.load();

		/* Thread Events */
		AtomicReference<PhysicsThread> thread = new AtomicReference<>();
		ServerLifecycleEvents.SERVER_STARTING.register(server -> thread.set(new PhysicsThread(server, "Server Physics Thread")));
		ServerLifecycleEvents.SERVER_STOPPING.register(server -> thread.get().destroy());
		ServerTickEvents.END_SERVER_TICK.register(server -> thread.get().tick());

		/* World Events */
		ServerWorldEvents.LOAD.register((server, world) -> ((SpaceStorage) world).setSpace(new MinecraftSpace(thread.get(), world)));
	}
}