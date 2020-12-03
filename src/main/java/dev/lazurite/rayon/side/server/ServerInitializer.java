package dev.lazurite.rayon.side.server;

import dev.lazurite.rayon.network.packet.PhysicsHandlerC2S;
import net.fabricmc.api.ModInitializer;

public class ServerInitializer implements ModInitializer {
	public static final String MODID = "rayon";

	@Override
	public void onInitialize() {
		PhysicsHandlerC2S.register();
	}
}
