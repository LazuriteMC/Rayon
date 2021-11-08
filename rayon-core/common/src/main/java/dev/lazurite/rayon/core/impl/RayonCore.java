package dev.lazurite.rayon.core.impl;

import dev.lazurite.rayon.core.impl.bullet.natives.NativeLoader;
import dev.lazurite.rayon.core.impl.bullet.thread.PhysicsThread;
import dev.lazurite.rayon.core.impl.event.ClientEventHandler;
import dev.lazurite.rayon.core.impl.event.ServerEventHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RayonCore {
	public static final String MODID = "rayon_core";
	public static final Logger LOGGER = LogManager.getLogger("Rayon Core");

	public static void init() {
		NativeLoader.load();
//		BlockProps.load();
		ServerEventHandler.register();
	}

	/*
	depth (m) * water density (kg/m3) * gravity (m/s2) = pressure (Pa)
	pressure (Pa) * area (m2) = force (N)
	 */

	public static PhysicsThread getThread(boolean isClient) {
		return isClient ? ClientEventHandler.getThread() : ServerEventHandler.getThread();
	}
}