package dev.lazurite.rayon.core.impl.forge;

import dev.lazurite.rayon.core.impl.RayonCore;
import dev.lazurite.rayon.core.impl.RayonCoreClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(RayonCore.MODID)
@Mod.EventBusSubscriber(modid = RayonCore.MODID, value = Dist.CLIENT)
public class RayonCoreForge {
    public RayonCoreForge() {
        RayonCore.init();
//        MinecraftForge.EVENT_BUS.register(this);
    }

//    @SubscribeEvent
//    public void onInitializeClient() {
//        RayonCoreClient.init();
//    }
}
