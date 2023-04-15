package dev.lazurite.rayon.impl.bullet.thread.util;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.Minecraft;

public class ClientUtil {
    @ExpectPlatform
    public static boolean isClient() {
        throw new AssertionError();
    }

    public static boolean isPaused() {
        if (isClient()) {
            return Minecraft.getInstance().isPaused();
        }
        return false;
    }

    public static boolean isConnectedToServer() {
        if (isClient()) {
            return Minecraft.getInstance().getConnection() != null;
        }
        return false;
    }
}
