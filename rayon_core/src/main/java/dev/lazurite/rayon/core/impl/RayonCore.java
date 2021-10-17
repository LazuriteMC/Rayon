package dev.lazurite.rayon.core.impl;

import dev.lazurite.rayon.core.impl.bullet.natives.NativeLoader;
import dev.lazurite.rayon.core.impl.bullet.thread.PhysicsThread;
import dev.lazurite.rayon.core.impl.event.ClientEventHandler;
import dev.lazurite.rayon.core.impl.event.ServerEventHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(RayonCore.MODID)
public class RayonCore{
	public static final String MODID = "rayon_core";
	public static final Logger LOGGER = LogManager.getLogger("Rayon Core");

	public RayonCore(){
		LOGGER.info("Rayon Core Activated!");
		MinecraftForge.EVENT_BUS.register(this);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onInitialize);
	}

	@SubscribeEvent
	public void onInitialize(FMLCommonSetupEvent event) {
		NativeLoader.load();
	}

	public static PhysicsThread getThread(boolean isClient) {
		return isClient ? ClientEventHandler.getThread() : ServerEventHandler.getThread();
	}

	public static boolean isImmersivePortalsPresent() {
		return ModList.get().isLoaded("immersive_portals");
	}
}