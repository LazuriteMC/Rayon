package dev.lazurite.rayon.mixin.common;

import dev.lazurite.rayon.physics.body.entity.DynamicBodyEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.vecmath.Vector3f;

@Mixin(Entity.class)
public class EntityMixin {
    @Unique private final Entity entity = (Entity) (Object) this;
    @Shadow private Vec3d velocity;
    @Shadow public boolean velocityDirty;

    @Inject(method = "setPos", at = @At("HEAD"), cancellable = true)
    public void setPos(double x, double y, double z, CallbackInfo info) {
//        DynamicBodyEntity dynamicBody = DynamicBodyEntity.get(entity);
//
//        if (dynamicBody != null) {
//            dynamicBody.setPosition(new Vector3f((float) x, (float) y, (float) z));
//        }

        if (DynamicBodyEntity.get(entity) != null) {
            info.cancel();
        }
    }

    @Inject(method = "updatePosition", at = @At("HEAD"), cancellable = true)
    public void updatePosition(double x, double y, double z, CallbackInfo info) {
        if (DynamicBodyEntity.get(entity) != null) {
            info.cancel();
        }
    }

    @Inject(method = "move", at = @At("HEAD"), cancellable = true)
    public void move(MovementType type, Vec3d movement, CallbackInfo info) {
//        if (DynamicBodyEntity.get(entity) != null) {
//            info.cancel();
//        }
    }

    @Inject(method = "setVelocity(Lnet/minecraft/util/math/Vec3d;)V", at = @At("TAIL"))
    public void setVelocity(Vec3d velocity, CallbackInfo info) {
        if (DynamicBodyEntity.get(entity) != null) {
            this.velocity = Vec3d.ZERO;
            this.velocityDirty = false;
        }
    }
}
