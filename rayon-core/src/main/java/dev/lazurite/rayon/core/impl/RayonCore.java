package dev.lazurite.rayon.core.impl;

import dev.lazurite.rayon.core.impl.physics.PhysicsThread;
import dev.lazurite.rayon.core.impl.util.BlockProps;
import dev.lazurite.rayon.core.impl.util.lifecycle.ServerLifecycleHandler;
import dev.lazurite.rayon.core.impl.util.NativeLoader;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The common entrypoint for Rayon Core.
 * @see RayonCoreClient
 * @see NativeLoader
 * @see BlockProps
 * @see ServerLifecycleHandler
 */
public class RayonCore implements ModInitializer {
	public static final String MODID = "rayon-core";
	public static final Logger LOGGER = LogManager.getLogger("Rayon Core");

	@Override
	public void onInitialize() {
		NativeLoader.load();
		BlockProps.load();

		// Server Events
		ServerLifecycleEvents.SERVER_STOPPING.register(ServerLifecycleHandler::onServerStop);
		ServerLifecycleEvents.SERVER_STARTING.register(ServerLifecycleHandler::onServerStart);
		ServerTickEvents.END_SERVER_TICK.register(ServerLifecycleHandler::onServerTick);

		// World Events
		ServerWorldEvents.LOAD.register(ServerLifecycleHandler::onWorldLoad);
		ServerTickEvents.START_WORLD_TICK.register(ServerLifecycleHandler::onWorldTick);
	}

	public static PhysicsThread getThread() {
		return ServerLifecycleHandler.getThread();
	}

	public static boolean isImmersivePortalsPresent() {
		return FabricLoader.getInstance().isModLoaded("immersive_portals");
	}
}