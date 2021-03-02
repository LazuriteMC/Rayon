package dev.lazurite.rayon.impl.element.entity.hooks;

import dev.lazurite.rayon.api.element.PhysicsElement;
import dev.lazurite.rayon.impl.Rayon;
import dev.lazurite.rayon.impl.bullet.body.ElementRigidBody;
import dev.lazurite.rayon.impl.util.math.VectorHelper;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Allows {@link PhysicsElement} objects to be affected by explosions.
 */
@Mixin(Explosion.class)
public class ExplosionMixin {
    @Redirect(
            method = "collectBlocksAndDamageEntities",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V"
            )
    )
    public void setVelocity(Entity entity, Vec3d velocity) {
        if (entity instanceof PhysicsElement) {
            ElementRigidBody rigidBody = ((PhysicsElement) entity).getRigidBody();

            Rayon.THREAD.get(entity.getEntityWorld()).execute(space ->
                rigidBody.applyCentralImpulse(VectorHelper.vec3dToVector3f(velocity).multLocal(rigidBody.getMass()).multLocal(100))
            );
        }
    }
}
