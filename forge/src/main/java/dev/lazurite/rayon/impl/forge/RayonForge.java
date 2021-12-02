package dev.lazurite.rayon.impl.forge;

import dev.lazurite.rayon.impl.Rayon;
import dev.lazurite.rayon.impl.bullet.collision.body.entity.forge.handler.EntityNetworkingClientHandler;
import dev.lazurite.rayon.impl.bullet.collision.body.entity.forge.handler.EntityNetworkingServerHandler;
import dev.lazurite.rayon.impl.event.forge.ClientEventHandlerImpl;
import dev.lazurite.rayon.impl.event.forge.ServerEventHandlerImpl;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Rayon.MODID)
public class RayonForge {
    public RayonForge() {
        Rayon.init();
        EntityNetworkingServerHandler.register();
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
        MinecraftForge.EVENT_BUS.register(ClientEventHandlerImpl.class);
        MinecraftForge.EVENT_BUS.register(ServerEventHandlerImpl.class);
    }

    @SubscribeEvent
    public void onInitializeClient(FMLClientSetupEvent event) {
        Rayon.initClient();
        EntityNetworkingClientHandler.register();
    }
}