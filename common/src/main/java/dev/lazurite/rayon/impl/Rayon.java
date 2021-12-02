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
//		BlockProps.load();
		ServerEventHandler.register();
	}

	public static void initClient() {
		ClientEventHandler.register();
	}

	/*
	depth (m) * water density (kg/m3) * gravity (m/s2) = pressure (Pa)
	pressure (Pa) * area (m2) = force (N)
	 */

	public static PhysicsThread getThread(boolean isClient) {
		return isClient ? ClientEventHandler.getThread() : ServerEventHandler.getThread();
	}
}