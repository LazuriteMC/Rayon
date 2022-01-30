package dev.lazurite.rayon.impl;

import dev.lazurite.rayon.impl.bullet.natives.NativeLoader;
import dev.lazurite.rayon.impl.bullet.thread.PhysicsThread;
import dev.lazurite.rayon.impl.event.ClientEventHandler;
import dev.lazurite.rayon.impl.event.ServerEventHandler;
import dev.lazurite.rayon.impl.event.network.EntityNetworking;
import dev.lazurite.transporter.impl.Transporter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Rayon {
	public static final String MODID = "rayon";
	public static final Logger LOGGER = LogManager.getLogger("Rayon");

	public static void intialize() {
		NativeLoader.load();
		Transporter.initialize();
		EntityNetworking.register();
		ServerEventHandler.register();
	}

	public static void initializeClient() {
		ClientEventHandler.register();
		EntityNetworking.registerClient();
	}

	public static PhysicsThread getThread(boolean isClient) {
		return isClient ? ClientEventHandler.getThread() : ServerEventHandler.getThread();
	}
}