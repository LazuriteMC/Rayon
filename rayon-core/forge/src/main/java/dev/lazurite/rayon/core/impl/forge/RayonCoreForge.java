package dev.lazurite.rayon.core.impl.forge;

import dev.lazurite.rayon.core.impl.RayonCore;
import dev.lazurite.rayon.core.impl.RayonCoreClient;
import dev.lazurite.rayon.core.impl.event.forge.ClientEventHandlerImpl;
import dev.lazurite.rayon.core.impl.event.forge.ServerEventHandlerImpl;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(RayonCore.MODID)
public class RayonCoreForge {
    public RayonCoreForge() {
        RayonCore.init();
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
        MinecraftForge.EVENT_BUS.register(ClientEventHandlerImpl.class);
        MinecraftForge.EVENT_BUS.register(ServerEventHandlerImpl.class);
    }

    @SubscribeEvent
    public void onInitializeClient(FMLClientSetupEvent event) {
        RayonCoreClient.init();
    }
}