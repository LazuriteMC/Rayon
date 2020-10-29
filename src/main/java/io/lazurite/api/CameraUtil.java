package io.lazurite.api;

import io.lazurite.api.client.LazuriteClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class CameraUtil {
    private static final MinecraftClient client = LazuriteClient.client;
    private static boolean isLocked = false;

    public static void lock(Entity entity) {
        client.setCameraEntity(entity);
        isLocked = true;
    }

    public static void unlock() {
        isLocked = false;
        client.setCameraEntity(client.player);
    }

    public static boolean isLocked() {
        return isLocked;
    }
}
