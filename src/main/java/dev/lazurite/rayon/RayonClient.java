package dev.lazurite.rayon;

import dev.lazurite.rayon.physics.composition.PhysicsComposition;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

@Environment(EnvType.CLIENT)
public class RayonClient {
    public static boolean belongsTo(Entity entity) {
        PlayerEntity player = MinecraftClient.getInstance().player;
        PhysicsComposition physics = Rayon.getPhysics(entity);

        if (physics != null && player != null) {
            return physics.belongsTo(player);
        }

        return false;
    }
}
