package dev.lazurite.rayon.impl;

import dev.lazurite.rayon.impl.bullet.natives.NativeLoader;
import dev.lazurite.rayon.impl.bullet.thread.PhysicsThread;
import dev.lazurite.rayon.impl.event.ClientEventHandler;
import dev.lazurite.rayon.impl.event.ServerEventHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Rayon {
	public static final String MODID = "rayon";
	public static final Logger LOGGER = LogManager.getLogger("Rayon");

	public static void init() {
		NativeLoader.load();
		ServerEventHandler.register();
	}

	public static void initClient() {
		ClientEventHandler.register();
	}

	public static PhysicsThread getThread(boolean isClient) {
		return isClient ? ClientEventHandler.getThread() : ServerEventHandler.getThread();
	}
}