package dev.lazurite.rayon.impl.util.compat.forge;

import dev.lazurite.rayon.impl.util.compat.ImmersivePortalsUtil;
import net.minecraftforge.fml.loading.FMLLoader;

public class ImmersivePortalsUtilImpl {
    public static boolean isImmersivePortalsPresent() {
        return FMLLoader.getLoadingModList().getModFileById(ImmersivePortalsUtil.MODID) != null;
    }
}