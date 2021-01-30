package dev.lazurite.rayon.impl.mixin.common;

import com.jme3.math.Vector3f;
import dev.lazurite.rayon.Rayon;
import dev.lazurite.rayon.impl.physics.body.EntityRigidBody;
import dev.lazurite.rayon.impl.physics.world.MinecraftDynamicsWorld;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Entity.class)
public abstract class EntityMixin {
    /**
     * This allows non-physics entities to interact with {@link EntityRigidBody}s.
     */
    @Inject(
            method = "pushAwayFrom",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;addVelocity(DDD)V",
                    shift = At.Shift.AFTER,
                    ordinal = 1
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    public void pushAwayFrom(Entity entity, CallbackInfo info, double d, double e) {
        if (EntityRigidBody.is((Entity) (Object) this) && !EntityRigidBody.is(entity)) {
            Rayon.ENTITY.get((Entity) (Object) this).applyCentralImpulse(new Vector3f((float) -d * 100, 0.0f, (float) -e * 100));
        }
    }

    /**
     * This method makes sure that the default behavior for
     * {@link EntityRigidBody}s is to be collidable.
     */
    @Inject(method = "collides", at = @At("HEAD"), cancellable = true)
    public void collides(CallbackInfoReturnable<Boolean> info) {
        if (EntityRigidBody.is((Entity) (Object) this)) {
            info.setReturnValue(false);
        }
    }

    /**
     * This method makes sure that the default behavior for
     * {@link EntityRigidBody}s is to be pushable.
     */
    @Inject(method = "isPushable", at = @At("HEAD"), cancellable = true)
    public void isPushable(CallbackInfoReturnable<Boolean> info) {
        if (EntityRigidBody.is((Entity) (Object) this)) {
            info.setReturnValue(true);
        }
    }

    /**
     * This method cleans up after the {@link MinecraftDynamicsWorld}
     * by removing any {@link EntityRigidBody}s that had their own
     * {@link Entity} removed from the world.
     */
    @Inject(method = "remove", at = @At("HEAD"))
    public void remove(CallbackInfo info) {
        if (EntityRigidBody.is((Entity) (Object) this)) {
            Rayon.WORLD.get(((Entity) (Object) this).getEntityWorld()).removeCollisionObject(Rayon.ENTITY.get((Entity) (Object) this));
        }
    }
}
