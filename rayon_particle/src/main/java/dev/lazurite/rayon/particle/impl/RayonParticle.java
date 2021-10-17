package dev.lazurite.rayon.particle.impl;

import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(RayonParticle.MODID)
public class RayonParticle{
    public static final String MODID = "testmod_particle";
    public static final Logger LOGGER = LogManager.getLogger("Rayon Particle Test mod");

    public RayonParticle(){
        LOGGER.info("Rayon Particle Test mod activated!");
    }
}
