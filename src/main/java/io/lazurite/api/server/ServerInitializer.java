package io.lazurite.api.server;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.server.ServerStartCallback;
import net.minecraft.server.MinecraftServer;

public class ServerInitializer implements ModInitializer {
	public static final String MODID = "lazurite-api";

	/**The Minecraft Server for use in other classes */
	public static MinecraftServer server;

	@Override
	public void onInitialize() {
		ServerStartCallback.EVENT.register(ServerInitializer::start);
	}

	public static void start(MinecraftServer server) {
		ServerInitializer.server = server;
	}
}
