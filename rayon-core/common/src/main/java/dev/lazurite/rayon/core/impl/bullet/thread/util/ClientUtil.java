package dev.lazurite.rayon.core.impl.bullet.thread.util;

import dev.architectury.injectables.annotations.ExpectPlatform;

public class ClientUtil {
    @ExpectPlatform
    public static boolean isPaused() {
        throw new AssertionError();
    }
}
