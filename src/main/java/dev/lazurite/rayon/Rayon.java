package dev.lazurite.rayon;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Rayon.MODID)
public class Rayon {
    public static final String MODID = "rayon";
    public static final Logger LOGGER = LogManager.getLogger("Rayon");

    @SubscribeEvent
    public void onFMLCommonSetup(FMLCommonSetupEvent event){
        LOGGER.info("Rayon activated");
    }
}
