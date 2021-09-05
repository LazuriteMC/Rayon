package dev.lazurite.rayon.core.impl;

import dev.lazurite.rayon.core.impl.bullet.collision.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.bullet.natives.NativeLoader;
import dev.lazurite.rayon.core.impl.bullet.thread.PhysicsThread;
import dev.lazurite.rayon.core.impl.util.debug.CollisionObjectDebugger;
import dev.lazurite.rayon.core.impl.util.BlockProps;
import dev.lazurite.rayon.core.impl.event.ClientEventHandler;
import dev.lazurite.rayon.core.impl.event.ServerEventHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RayonCore implements ModInitializer, ClientModInitializer {
	public static final String MODID = "rayon-core";
	public static final Logger LOGGER = LogManager.getLogger("Rayon Core");

	@Override
	public void onInitialize() {
		NativeLoader.load();
		BlockProps.load();
		ServerEventHandler.register();
	}

	@Override
	public void onInitializeClient() {
		ClientEventHandler.register();

		WorldRenderEvents.BEFORE_DEBUG_RENDER.register(context -> {
			if (CollisionObjectDebugger.getInstance().isEnabled()) {
				CollisionObjectDebugger.getInstance().renderSpace(MinecraftSpace.get(context.world()), context.tickDelta());
			}
		});
	}

	public static PhysicsThread getThread(boolean isClient) {
		return isClient ? ClientEventHandler.getThread() : ServerEventHandler.getThread();
	}

	public static boolean isImmersivePortalsPresent() {
		return FabricLoader.getInstance().isModLoaded("immersive_portals");
	}
}