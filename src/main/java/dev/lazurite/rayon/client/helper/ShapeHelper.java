package dev.lazurite.rayon.client.helper;

import com.bulletphysics.collision.shapes.CollisionShape;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class ShapeHelper {
    public static final MinecraftClient client = MinecraftClient.getInstance();

    public static CollisionShape getEntityShape(Entity entity) {
        return null;
    }
}
