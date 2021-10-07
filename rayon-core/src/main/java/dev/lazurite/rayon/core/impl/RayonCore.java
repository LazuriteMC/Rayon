package dev.lazurite.rayon.core.impl;

import dev.lazurite.rayon.core.impl.bullet.natives.NativeLoader;
import dev.lazurite.rayon.core.impl.bullet.thread.PhysicsThread;
import dev.lazurite.rayon.core.impl.event.ClientEventHandler;
import dev.lazurite.rayon.core.impl.event.ServerEventHandler;
import dev.lazurite.rayon.core.impl.util.BlockProps;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(RayonCore.MODID)
public class RayonCore{
	public static final String MODID = "rayon-core";
	public static final Logger LOGGER = LogManager.getLogger("Rayon Core");

	@SubscribeEvent
	public void onInitialize(FMLCommonSetupEvent event) {
		NativeLoader.load();
		BlockProps.load();
		ServerEventHandler.register();
	}

	public static PhysicsThread getThread(boolean isClient) {
		return isClient ? ClientEventHandler.getThread() : ServerEventHandler.getThread();
	}

	public static boolean isImmersivePortalsPresent() {
		return ModList.get().isLoaded("immersive_portals");
	}
}