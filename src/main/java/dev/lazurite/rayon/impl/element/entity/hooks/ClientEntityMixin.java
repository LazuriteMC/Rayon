package dev.lazurite.rayon.impl.element.entity.hooks;

import dev.lazurite.rayon.api.element.PhysicsElement;
import dev.lazurite.rayon.impl.bullet.body.ElementRigidBody;
import dev.lazurite.rayon.impl.element.entity.net.EntityElementMovementC2S;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * This mixin is for the client only. It syncs movement data with the
 * server if the priority player is set.
 * @see CommonEntityMixin
 */
@Mixin(Entity.class)
@Environment(EnvType.CLIENT)
public class ClientEntityMixin {
    @Shadow public World world;

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo info) {
        if (world.isClient() && this instanceof PhysicsElement && MinecraftClient.getInstance().player != null) {
            ElementRigidBody body = ((PhysicsElement) this).getRigidBody();

            if (MinecraftClient.getInstance().player.equals(body.getPriorityPlayer())) {
                if (body.isActive()) {
                    EntityElementMovementC2S.send((PhysicsElement) this);
                }
            }
        }
    }
}
