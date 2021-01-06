package dev.lazurite.rayon.mixin.common.entity;

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
        dev.lazurite.rayon.physics.body.entity.DynamicBodyEntity dynamicEntity = dev.lazurite.rayon.physics.body.entity.DynamicBodyEntity.get((Entity) (Object) this);

        if (dynamicEntity != null) {
            dynamicEntity.applyCentralForce(new Vector3f((float) -d * 500, 0.0f, (float) -e * 500));
        }
    }

    @Inject(method = "collides()Z", at = @At("HEAD"), cancellable = true)
    public void collides(CallbackInfoReturnable<Boolean> info) {
        dev.lazurite.rayon.physics.body.entity.DynamicBodyEntity dynamicEntity = dev.lazurite.rayon.physics.body.entity.DynamicBodyEntity.get((Entity) (Object) this);

        if (dynamicEntity != null) {
            info.setReturnValue(true);
        }
    }

    @Inject(method = "isPushable()Z", at = @At("HEAD"), cancellable = true)
    public void isPushable(CallbackInfoReturnable<Boolean> info) {
        dev.lazurite.rayon.physics.body.entity.DynamicBodyEntity dynamicEntity = dev.lazurite.rayon.physics.body.entity.DynamicBodyEntity.get((Entity) (Object) this);

        if (dynamicEntity != null) {
            info.setReturnValue(true);
        }
    }
}
