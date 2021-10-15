package dev.lazurite.rayon.entity.impl;

import dev.lazurite.rayon.entity.impl.event.ClientEventHandler;
import dev.lazurite.rayon.entity.impl.event.ServerEventHandler;
import dev.lazurite.rayon.entity.impl.network.RayonEntityPacketHandler;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The common entrypoint for Rayon Entity.
 */
@Mod(RayonEntity.MODID)
public class RayonEntity{
	public static final String MODID = "rayon-entity";
	public static final Logger LOGGER = LogManager.getLogger("Rayon Entity");

	public RayonEntity(){
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		bus.addListener(this::onInitialize);
		bus.addListener(this::onInitializeClient);
		RayonEntityPacketHandler.registerPackets();
		LOGGER.info("Rayon Entity initialized");
	}

	public void onInitialize(FMLCommonSetupEvent event) {
		ServerEventHandler.register();
	}

	public void onInitializeClient(FMLClientSetupEvent event) {
		ClientEventHandler.register();
	}
}