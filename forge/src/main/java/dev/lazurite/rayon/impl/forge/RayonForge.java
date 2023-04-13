package dev.lazurite.rayon.impl.forge;

import dev.lazurite.rayon.impl.Rayon;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Rayon.MODID)
public class RayonForge {
    public RayonForge() {
        Rayon.intialize();
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
    }

    @SubscribeEvent
    public void onInitializeClient(FMLClientSetupEvent event) {
        Rayon.initializeClient();
    }
}