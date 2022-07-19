package dev.lazurite.rayon.impl.mixin.common.entity;

import dev.lazurite.rayon.api.EntityPhysicsElement;
import dev.lazurite.rayon.api.PhysicsElement;
import dev.lazurite.rayon.impl.bullet.math.Convert;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Set;

/**
 * Allows {@link PhysicsElement} objects to be affected by explosions.
 */
@Mixin(Explosion.class)
public class ExplosionMixin {
    @Unique
    private Entity entity;

    @Inject(
            method = "explode",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;ignoreExplosion()Z"
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    public void collectBlocksAndDamageEntities(CallbackInfo info, Set set, int q, float r, int s, int t, int u, int v, int w, int x, List list, Vec3 vec3, int y, Entity entity) {
        this.entity = entity;
    }

    @ModifyArg(
            method = "explode",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V"
            )
    )
    public Vec3 setVelocity(Vec3 velocity) {
        if (entity instanceof EntityPhysicsElement element) {
            element.getRigidBody().applyCentralImpulse(Convert.toBullet(velocity).multLocal(element.getRigidBody().getMass() * 100f));
        }

        return velocity;
    }
}