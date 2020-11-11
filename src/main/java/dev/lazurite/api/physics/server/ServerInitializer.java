package dev.lazurite.api.physics.server;

import dev.lazurite.api.physics.network.packet.PhysicsHandlerC2S;
import dev.lazurite.api.physics.network.tracker.generic.GenericType;
import dev.lazurite.api.physics.network.tracker.generic.types.BooleanType;
import dev.lazurite.api.physics.network.tracker.generic.types.FloatType;
import dev.lazurite.api.physics.network.tracker.generic.types.IntegerType;
import net.fabricmc.api.ModInitializer;


public class ServerInitializer implements ModInitializer {
	public static final String MODID = "lazurite-api";

	/* Data Types */
	public static final GenericType<Integer> INTEGER_TYPE = new IntegerType();
	public static final GenericType<Float> FLOAT_TYPE = new FloatType();
	public static final GenericType<Boolean> BOOLEAN_TYPE = new BooleanType();

	@Override
	public void onInitialize() {
		PhysicsHandlerC2S.register();
		ServerTick.register();
	}
}
