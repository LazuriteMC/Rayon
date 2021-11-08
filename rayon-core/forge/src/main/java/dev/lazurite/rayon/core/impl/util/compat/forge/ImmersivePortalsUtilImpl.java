package dev.lazurite.rayon.core.impl.util.compat.forge;

import dev.lazurite.rayon.core.impl.util.compat.ImmersivePortalsUtil;
import net.minecraftforge.fml.loading.FMLLoader;

public class ImmersivePortalsUtilImpl {
    public static boolean isImmersivePortalsPresent() {
        return FMLLoader.getLoadingModList().getModFileById(ImmersivePortalsUtil.MODID) != null;
    }
}