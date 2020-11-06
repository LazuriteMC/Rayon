package io.lazurite.api.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class CameraLock {
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