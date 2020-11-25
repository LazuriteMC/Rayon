package dev.lazurite.api.physics.server;

import dev.lazurite.api.physics.network.packet.PhysicsHandlerC2S;
import net.fabricmc.api.ModInitializer;

public class ServerInitializer implements ModInitializer {
	public static final String MODID = "lazurite-physics";
	public static final String VERSION = "1.0.0";
	public static final String URL = "https://github.com/LazuriteMC/Lazurite-Physics-API/releases";

	@Override
	public void onInitialize() {
		PhysicsHandlerC2S.register();
	}
}
