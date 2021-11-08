package dev.lazurite.rayon.core.impl.util.compat.fabric;

import dev.lazurite.rayon.core.impl.util.compat.ImmersivePortalsUtil;
import net.fabricmc.loader.api.FabricLoader;

public class ImmersivePortalsUtilImpl {

    public static boolean isImmersivePortalsPresent() {
        return FabricLoader.getInstance().isModLoaded(ImmersivePortalsUtil.MODID);
    }
}