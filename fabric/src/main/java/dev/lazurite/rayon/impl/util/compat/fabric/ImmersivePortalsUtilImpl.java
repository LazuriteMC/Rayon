package dev.lazurite.rayon.impl.util.compat.fabric;

import dev.lazurite.rayon.impl.util.compat.ImmersivePortalsUtil;
import net.fabricmc.loader.api.FabricLoader;

public class ImmersivePortalsUtilImpl {
    public static boolean isImmersivePortalsPresent() {
        return FabricLoader.getInstance().getModContainer(ImmersivePortalsUtil.MODID).isPresent();
    }
}