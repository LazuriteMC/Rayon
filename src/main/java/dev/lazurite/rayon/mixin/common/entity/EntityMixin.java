package dev.lazurite.rayon.mixin.common.entity;

import dev.lazurite.rayon.physics.body.entity.DynamicBodyEntity;
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
    @Inject(
            method = "pushAwayFrom",
            locals = LocalCapture.CAPTURE_FAILHARD,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;addVelocity(DDD)V",
                    shift = At.Shift.AFTER,
                    ordinal = 1
            )
    )
    public void pushAwayFrom(Entity entity, CallbackInfo info, double d, double e) {
        if (DynamicBodyEntity.is((Entity) (Object) this) && !DynamicBodyEntity.is(entity)) {
            DynamicBodyEntity.get((Entity) (Object) this).applyCentralForce(new Vector3f((float) -d * 500, 0.0f, (float) -e * 500));
        }
    }

    @Inject(method = "collides()Z", at = @At("HEAD"), cancellable = true)
    public void collides(CallbackInfoReturnable<Boolean> info) {
        DynamicBodyEntity dynamicEntity = DynamicBodyEntity.get((Entity) (Object) this);

        if (dynamicEntity != null) {
            info.setReturnValue(true);
        }
    }

    @Inject(method = "isPushable()Z", at = @At("HEAD"), cancellable = true)
    public void isPushable(CallbackInfoReturnable<Boolean> info) {
        DynamicBodyEntity dynamicEntity = DynamicBodyEntity.get((Entity) (Object) this);

        if (dynamicEntity != null) {
            info.setReturnValue(true);
        }
    }

    @Inject(method = "remove()V", at = @At("HEAD"))
    public void remove(CallbackInfo info) {
        DynamicBodyEntity dynamicEntity = DynamicBodyEntity.get((Entity) (Object) this);

        if (dynamicEntity != null) {
            MinecraftDynamicsWorld.get(((Entity) (Object) this).getEntityWorld()).removeRigidBody(dynamicEntity);
        }
    }
}
