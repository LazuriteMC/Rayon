package dev.lazurite.rayon.mixin.common.entity;

import dev.lazurite.rayon.physics.body.EntityRigidBody;
import dev.lazurite.rayon.physics.world.MinecraftDynamicsWorld;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import javax.vecmath.Vector3f;

@Mixin(Entity.class)
public class EntityMixin {
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
            EntityRigidBody.get((Entity) (Object) this).applyCentralImpulse(new Vector3f((float) -d * 100, 0.0f, (float) -e * 100));
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
     * by removing any any {@link EntityRigidBody}s that were removed.
     */
    @Inject(method = "remove", at = @At("HEAD"))
    public void remove(CallbackInfo info) {
        if (EntityRigidBody.is((Entity) (Object) this)) {
            MinecraftDynamicsWorld.get(((Entity) (Object) this).getEntityWorld()).removeRigidBody(EntityRigidBody.get((Entity) (Object) this));
        }
    }
}
