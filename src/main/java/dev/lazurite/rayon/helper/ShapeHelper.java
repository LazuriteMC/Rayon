package dev.lazurite.rayon.helper;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;

import javax.vecmath.Vector3f;

@Environment(EnvType.CLIENT)
public class ShapeHelper {
    public static final MinecraftClient client = MinecraftClient.getInstance();

    public static CollisionShape getEntityShape(EntityType<?> type) {
        return new BoxShape(new Vector3f(0.5f, 0.5f, 0.5f));
    }
}
