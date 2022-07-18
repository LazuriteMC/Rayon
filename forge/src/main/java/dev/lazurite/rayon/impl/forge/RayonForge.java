package dev.lazurite.rayon.impl.forge;

import dev.lazurite.rayon.impl.Rayon;
import dev.lazurite.rayon.impl.forge.dev.RayonDevClientForge;
import dev.lazurite.rayon.impl.forge.dev.RayonDevForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;

@Mod(Rayon.MODID)
public class RayonForge {
    public RayonForge() {
        Rayon.intialize();
        FMLJavaModLoadingContext.get().getModEventBus().register(this);

        if (!FMLLoader.isProduction()) {
            RayonDevForge.init();
        }
    }

    @SubscribeEvent
    public void onInitializeClient(FMLClientSetupEvent event) {
        Rayon.initializeClient();

        if (!FMLLoader.isProduction()) {
            RayonDevClientForge.init();
        }
    }
}