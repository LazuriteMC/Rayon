package dev.lazurite.rayon.entity.impl;

import dev.lazurite.rayon.core.impl.RayonCore;
import dev.lazurite.rayon.entity.impl.event.ClientEventHandler;
import dev.lazurite.rayon.entity.impl.event.ServerEventHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The common entrypoint for Rayon Entity.
 */
public class RayonEntity implements ModInitializer, ClientModInitializer {
	public static final String MODID = "rayon-entity";
	public static final Logger LOGGER = LogManager.getLogger("Rayon Entity");

	public static final ResourceLocation PROPERTIES_PACKET = new ResourceLocation(RayonCore.MODID, "element_properties");
	public static final ResourceLocation MOVEMENT_PACKET = new ResourceLocation(MODID, "element_movement_update");

	@Override
	public void onInitialize() {
		ServerEventHandler.register();
	}

	@Override
	public void onInitializeClient() {
		ClientEventHandler.register();
	}
}