package dev.lazurite.api;

import dev.lazurite.api.network.packet.ConfigC2S;
import dev.lazurite.api.network.packet.PhysicsHandlerC2S;
import dev.lazurite.api.server.ServerTick;
import dev.lazurite.api.network.tracker.Config;
import dev.lazurite.api.network.tracker.generic.GenericType;
import dev.lazurite.api.network.tracker.generic.types.BooleanType;
import dev.lazurite.api.network.tracker.generic.types.FloatType;
import dev.lazurite.api.network.tracker.generic.types.IntegerType;
import net.fabricmc.api.ModInitializer;

import java.util.HashMap;
import java.util.UUID;

public class LazuriteAPI implements ModInitializer {
	public static final String MODID = "lazurite-api";

	/* Data Types */
	public static final GenericType<Integer> INTEGER_TYPE = new IntegerType();
	public static final GenericType<Float> FLOAT_TYPE = new FloatType();
	public static final GenericType<Boolean> BOOLEAN_TYPE = new BooleanType();

	/** Configs from players who joined the game */
	public static final HashMap<UUID, Config> SERVER_PLAYER_CONFIGS = new HashMap<>();

	@Override
	public void onInitialize() {
		PhysicsHandlerC2S.register();
		ConfigC2S.register();

		ServerTick.register();
	}
}
