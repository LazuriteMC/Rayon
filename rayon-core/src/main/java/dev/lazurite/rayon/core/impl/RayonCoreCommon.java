package dev.lazurite.rayon.core.impl;

import dev.lazurite.rayon.core.api.event.PhysicsSpaceEvents;
import dev.lazurite.rayon.core.impl.thread.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.thread.PhysicsThread;
import dev.lazurite.rayon.core.impl.thread.supplier.ServerWorldSupplier;
import dev.lazurite.rayon.core.impl.thread.util.ThreadStorage;
import dev.lazurite.rayon.core.impl.util.NativeLoader;
import dev.lazurite.rayon.core.impl.thread.space.util.SpaceStorage;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.atomic.AtomicReference;

/**
 * The common entrypoint for Rayon Core. Handles the server thread lifecycle,
 * {@link MinecraftSpace} creation, and bullet native loading
 * @see RayonCoreClient
 * @see NativeLoader
 */
public class RayonCoreCommon implements ModInitializer {
	public static final String MODID = "rayon-core";
	public static final Logger LOGGER = LogManager.getLogger("Rayon Core");

	@Override
	public void onInitialize() {
		NativeLoader.load();

		/* Thread Events */
		AtomicReference<PhysicsThread> thread = new AtomicReference<>();

		ServerTickEvents.END_SERVER_TICK.register(server -> {
			thread.get().tick();
			server.getWorlds().forEach(world -> MinecraftSpace.get(world).getEntityManager().tick());
		});

		ServerLifecycleEvents.SERVER_STOPPING.register(server -> thread.get().destroy());

		ServerLifecycleEvents.SERVER_STARTING.register(server -> {
			thread.set(new PhysicsThread(server, new ServerWorldSupplier(server), "Server Physics Thread"));
			((ThreadStorage) server).setPhysicsThread(thread.get());
		});

		/* World Events */
		ServerWorldEvents.LOAD.register((server, world) -> {
			PhysicsSpaceEvents.PREINIT.invoker().onPreInit(thread.get(), world);
			((SpaceStorage) world).putSpace(MinecraftSpace.MAIN, new MinecraftSpace(thread.get(), world));
		});
	}
}