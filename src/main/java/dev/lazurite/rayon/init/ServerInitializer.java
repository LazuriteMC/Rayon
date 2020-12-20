package dev.lazurite.rayon.init;

import dev.lazurite.rayon.physics.composition.DynamicBodyComposition;
import dev.lazurite.thimble.Thimble;
import net.fabricmc.api.ModInitializer;

public class ServerInitializer implements ModInitializer {
	public static final String MODID = "rayon";

	static {
		Thimble.register(DynamicBodyComposition::new);
	}

	@Override
	public void onInitialize() {

	}
}
