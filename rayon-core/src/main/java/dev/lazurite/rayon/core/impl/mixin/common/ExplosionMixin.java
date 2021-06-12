package dev.lazurite.rayon.core.impl.mixin.common;

import dev.lazurite.rayon.core.api.PhysicsElement;
import dev.lazurite.rayon.core.impl.bullet.collision.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.bullet.math.VectorHelper;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Set;

/**
 * Allows {@link PhysicsElement} objects to be affected by explosions.
 */
@Mixin(Explosion.class)
public class ExplosionMixin {
    @Unique private Entity entity;

    @Inject(
            method = "collectBlocksAndDamageEntities",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;isImmuneToExplosion()Z"
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    public void collectBlocksAndDamageEntities(CallbackInfo info, Set set, float q, int r, int s, int t, int u, int v, int w, List list, Vec3d vec3d, int x, Entity entity) {
        this.entity = entity;
    }

    @ModifyArg(
            method = "collectBlocksAndDamageEntities",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V"
            )
    )
    public Vec3d setVelocity(Vec3d velocity) {
        if (entity instanceof PhysicsElement) {
            var rigidBody = ((PhysicsElement) entity).getRigidBody();

            MinecraftSpace.get(entity.getEntityWorld()).getWorkerThread().execute(() ->
                rigidBody.applyCentralImpulse(VectorHelper.vec3dToVector3f(velocity).multLocal(rigidBody.getMass()).multLocal(100))
            );
        }

        return velocity;
    }
}
