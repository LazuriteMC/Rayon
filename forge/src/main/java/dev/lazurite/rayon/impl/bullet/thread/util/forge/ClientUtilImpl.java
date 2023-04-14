package dev.lazurite.rayon.impl.bullet.thread.util.forge;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLLoader;

public class ClientUtilImpl {
    public static boolean isClient() {
        return FMLLoader.getDist() == Dist.CLIENT;
    }
}