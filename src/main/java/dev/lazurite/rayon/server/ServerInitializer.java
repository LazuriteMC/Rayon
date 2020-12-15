package dev.lazurite.rayon.server;

import dev.lazurite.rayon.physics.composition.PhysicsComposition;
import dev.lazurite.thimble.Thimble;
import net.fabricmc.api.ModInitializer;

public class ServerInitializer implements ModInitializer {
	public static final String MODID = "rayon";

	static {
		Thimble.register(PhysicsComposition::new);
	}

	@Override
	public void onInitialize() {

	}
}
