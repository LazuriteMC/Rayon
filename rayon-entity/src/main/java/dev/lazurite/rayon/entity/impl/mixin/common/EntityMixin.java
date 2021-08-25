package dev.lazurite.rayon.entity.impl.mixin.common;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.core.impl.RayonCore;
import dev.lazurite.rayon.core.impl.bullet.collision.body.ElementRigidBody;
import dev.lazurite.rayon.core.impl.bullet.math.Converter;
import dev.lazurite.rayon.entity.api.EntityPhysicsElement;
import dev.lazurite.toolbox.api.math.QuaternionHelper;
import dev.lazurite.toolbox.api.math.VectorHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Basic changes for {@link EntityPhysicsElement}s. ({@link CallbackInfo#cancel()} go brrr)
 */
@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method = "getVelocity", at = @At("HEAD"), cancellable = true)
    public void getVelocity(CallbackInfoReturnable<Vec3d> info) {
        if (this instanceof EntityPhysicsElement && RayonCore.isImmersivePortalsPresent()) {
            info.setReturnValue(VectorHelper.toVec3d(Converter.toMinecraft(
                ((EntityPhysicsElement) this).getRigidBody().getLinearVelocity(new Vector3f()).multLocal(0.05f).multLocal(0.2f)
            )));
        }
    }

    @Inject(method = "pushAwayFrom", at = @At("HEAD"), cancellable = true)
    public void pushAwayFrom(Entity entity, CallbackInfo info) {
        if (this instanceof EntityPhysicsElement && entity instanceof EntityPhysicsElement) {
            info.cancel();
        }
    }

    @Inject(method = "move", at = @At("HEAD"), cancellable = true)
    public void move(MovementType type, Vec3d movement, CallbackInfo info) {
        if (this instanceof EntityPhysicsElement) {
            info.cancel();
        }
    }

    @Inject(method = "writeNbt", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;writeCustomDataToNbt(Lnet/minecraft/nbt/NbtCompound;)V"))
    public void toTag(NbtCompound tag, CallbackInfoReturnable<NbtCompound> info) {
        if (this instanceof EntityPhysicsElement) {
            ElementRigidBody rigidBody = ((EntityPhysicsElement) this).getRigidBody();
            tag.put("orientation", QuaternionHelper.toTag(Converter.toMinecraft(rigidBody.getPhysicsRotation(new Quaternion()))));
            tag.put("linear_velocity", VectorHelper.toTag(Converter.toMinecraft(rigidBody.getLinearVelocity(new Vector3f()))));
            tag.put("angular_velocity", VectorHelper.toTag(Converter.toMinecraft(rigidBody.getAngularVelocity(new Vector3f()))));
        }
    }

    @Inject(method = "readNbt", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;readCustomDataFromNbt(Lnet/minecraft/nbt/NbtCompound;)V"))
    public void fromTag(NbtCompound tag, CallbackInfo info) {
        if (this instanceof EntityPhysicsElement) {
            ElementRigidBody rigidBody = ((EntityPhysicsElement) this).getRigidBody();
            rigidBody.setPhysicsRotation(Converter.toBullet(QuaternionHelper.fromTag(tag.getCompound("orientation"))));
            rigidBody.setLinearVelocity(Converter.toBullet(VectorHelper.fromTag(tag.getCompound("linear_velocity"))));
            rigidBody.setAngularVelocity(Converter.toBullet(VectorHelper.fromTag(tag.getCompound("angular_velocity"))));
        }
    }
}
