package dev.lazurite.rayon.side.server;

import dev.lazurite.rayon.physics.composition.DynamicPhysicsComposition;
import dev.lazurite.rayon.physics.composition.StaticPhysicsComposition;
import dev.lazurite.thimble.Thimble;
import net.fabricmc.api.ModInitializer;

public class ServerInitializer implements ModInitializer {
	public static final String MODID = "rayon";

	static {
		Thimble.register(DynamicPhysicsComposition::new);
		Thimble.register(StaticPhysicsComposition::new);
	}

	@Override
	public void onInitialize() {

	}
}
