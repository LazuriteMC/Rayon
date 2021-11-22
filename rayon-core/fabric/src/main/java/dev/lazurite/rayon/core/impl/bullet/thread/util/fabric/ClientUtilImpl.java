package dev.lazurite.rayon.core.impl.bullet.thread.util.fabric;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;

public class ClientUtilImpl {
    public static boolean isPaused() {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            return Minecraft.getInstance().isPaused();
        }

        return false;
    }
}