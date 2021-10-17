package dev.lazurite.rayon.particle.testmod;

import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ParticleTestMod.MODID)
public class ParticleTestMod {
    public static final String MODID = "testmod_particle";
    public static final Logger LOGGER = LogManager.getLogger("Rayon Particle Test Mod");

    public ParticleTestMod(){
        LOGGER.info("Particle Test mod activated!");
    }
}
