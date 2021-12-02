package dev.lazurite.rayon.impl.bullet.thread.util.forge;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLLoader;

public class ClientUtilImpl {
    public static boolean isPaused() {
        if (FMLLoader.getDist() == Dist.CLIENT) {
            return Minecraft.getInstance().isPaused();
        }

        return false;
    }
}