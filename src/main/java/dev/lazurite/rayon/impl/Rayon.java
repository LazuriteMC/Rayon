package dev.lazurite.rayon.impl;

import dev.lazurite.rayon.impl.bullet.body.net.ElementMovementC2S;
import dev.lazurite.rayon.impl.bullet.body.net.ElementPropertiesS2C;
import dev.lazurite.rayon.impl.bullet.body.net.ElementMovementS2C;
import dev.lazurite.rayon.impl.util.NativeLoader;
import dev.lazurite.rayon.impl.util.thread.lifecycle.ClientThreadLifecycle;
import dev.lazurite.rayon.impl.util.thread.lifecycle.ServerThreadLifecycle;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The main entrypoint for Rayon. Mainly contains packet
 * registrations and the loading of bullet natives.
 * @see NativeLoader
 */
public class Rayon implements ModInitializer, ClientModInitializer {
	public static final String MODID = "rayon";
	public static final Logger LOGGER = LogManager.getLogger("Rayon");

	@Override
	public void onInitialize() {
		NativeLoader.load();
		ServerThreadLifecycle.register();
		ServerPlayNetworking.registerGlobalReceiver(ElementMovementC2S.PACKET_ID, ElementMovementC2S::accept);
	}

	@Override
	public void onInitializeClient() {
		ClientThreadLifecycle.register();
		ClientPlayNetworking.registerGlobalReceiver(ElementPropertiesS2C.PACKET_ID, ElementPropertiesS2C::accept);
		ClientPlayNetworking.registerGlobalReceiver(ElementMovementS2C.PACKET_ID, ElementMovementS2C::accept);
	}
}