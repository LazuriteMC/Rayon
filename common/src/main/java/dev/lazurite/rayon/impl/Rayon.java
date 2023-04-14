package dev.lazurite.rayon.impl;

import dev.lazurite.rayon.impl.bullet.natives.NativeLoader;
import dev.lazurite.rayon.impl.bullet.thread.PhysicsThread;
import dev.lazurite.rayon.impl.bullet.thread.util.ClientUtil;
import dev.lazurite.rayon.impl.event.ClientEventHandler;
import dev.lazurite.rayon.impl.event.ServerEventHandler;
import dev.lazurite.rayon.impl.event.network.EntityNetworking;
import dev.lazurite.toolbox.api.network.PacketRegistry;
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
	}

	public static void initializeClient() {
		ClientEventHandler.register();
		EntityNetworking.registerClient();

		// Rayon Server Detection
		PacketRegistry.registerClientbound(new ResourceLocation(MODID, "i_have_rayon"), ctx -> serverHasRayon = true);
	}

	public static PhysicsThread getThread(boolean isClient) {
		return isClient ? ClientEventHandler.getThread() : ServerEventHandler.getThread();
	}

	public static boolean serverHasRayon() {
		var isConnected = ClientUtil.isConnectedToServer();
		if (!isConnected) {
			serverHasRayon = false;
			return false;
		}
		return serverHasRayon;
	}
}