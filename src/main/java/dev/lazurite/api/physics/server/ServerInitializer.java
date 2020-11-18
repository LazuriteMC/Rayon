package dev.lazurite.api.physics.server;

import dev.lazurite.api.physics.network.packet.PhysicsHandlerC2S;
import net.fabricmc.api.ModInitializer;


public class ServerInitializer implements ModInitializer {
	public static final String MODID = "lazurite-api";

	@Override
	public void onInitialize() {
		PhysicsHandlerC2S.register();
		ServerTick.register();
	}
}
