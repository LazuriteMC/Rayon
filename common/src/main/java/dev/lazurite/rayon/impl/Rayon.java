package dev.lazurite.rayon.impl;

import dev.lazurite.rayon.impl.bullet.natives.NativeLoader;
import dev.lazurite.rayon.impl.bullet.thread.PhysicsThread;
import dev.lazurite.rayon.impl.event.ClientEventHandler;
import dev.lazurite.rayon.impl.event.ServerEventHandler;
import dev.lazurite.rayon.impl.event.network.EntityNetworking;
import dev.lazurite.toolbox.api.event.ClientEvents;
import dev.lazurite.toolbox.api.event.ServerEvents;
import dev.lazurite.toolbox.api.network.PacketRegistry;
import dev.lazurite.toolbox.api.network.ServerNetworking;
import dev.lazurite.transporter.impl.Transporter;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Rayon {
	public static final String MODID = "rayon";
	public static final Logger LOGGER = LogManager.getLogger("Rayon");

	private static boolean serverHasRayon = false;

	public static void intialize() {
		// prevent annoying libbulletjme spam
		java.util.logging.LogManager.getLogManager().reset();

		NativeLoader.load();
		Transporter.initialize();
		EntityNetworking.register();
		ServerEventHandler.register();

		// Rayon Server Detection
		ServerEvents.Lifecycle.JOIN.register(player -> {
			ServerNetworking.send(player, new ResourceLocation(MODID, "i_have_rayon"), buf -> {});
		});
	}

	public static void initializeClient() {
		ClientEventHandler.register();
		EntityNetworking.registerClient();

		// Rayon Server Detection
		PacketRegistry.registerClientbound(new ResourceLocation(MODID, "i_have_rayon"), ctx -> serverHasRayon = true);
		ClientEvents.Lifecycle.DISCONNECT.register((client, level) -> serverHasRayon = false);
	}

	public static PhysicsThread getThread(boolean isClient) {
		return isClient ? ClientEventHandler.getThread() : ServerEventHandler.getThread();
	}

	public static boolean serverHasRayon() {
		return serverHasRayon;
	}
}