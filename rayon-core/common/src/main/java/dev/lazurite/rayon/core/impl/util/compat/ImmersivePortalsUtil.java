package dev.lazurite.rayon.core.impl.util.compat;

import dev.architectury.injectables.annotations.ExpectPlatform;

public class ImmersivePortalsUtil {
    public static final String MODID = "immersive_portals";

    @ExpectPlatform
    public static boolean isImmersivePortalsPresent() {
        throw new AssertionError();
    }
}
