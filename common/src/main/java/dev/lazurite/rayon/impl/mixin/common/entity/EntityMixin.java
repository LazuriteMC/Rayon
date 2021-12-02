package dev.lazurite.rayon.impl.mixin.common.entity;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.api.EntityPhysicsElement;
import dev.lazurite.rayon.impl.bullet.math.Convert;
import dev.lazurite.toolbox.api.math.QuaternionHelper;
import dev.lazurite.toolbox.api.math.VectorHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;
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
    // TODO removed immersive portals code here.

    @Inject(method = "lerpMotion", at = @At("HEAD"), cancellable = true)
    public void lerpMotion(double x, double y, double z, CallbackInfo info) {
        if (this instanceof EntityPhysicsElement) {
            System.out.println("NOOOOOOOOOOOOOOOOOOOOOO");
            info.cancel();
        }
    }

    @Inject(method = "push(Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"), cancellable = true)
    public void pushAwayFrom(Entity entity, CallbackInfo info) {
        if (this instanceof EntityPhysicsElement && entity instanceof EntityPhysicsElement) {
            info.cancel();
        }
    }

    @Inject(method = "move", at = @At("HEAD"), cancellable = true)
    public void move(MoverType type, Vec3 movement, CallbackInfo info) {
        if (this instanceof EntityPhysicsElement) {
            info.cancel();
        }
    }

    @Inject(method = "saveWithoutId", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V"))
    public void saveWithoutId(CompoundTag tag, CallbackInfoReturnable<CompoundTag> info) {
        if (this instanceof EntityPhysicsElement) {
            final var rigidBody = ((EntityPhysicsElement) this).getRigidBody();
            tag.put("orientation", QuaternionHelper.toTag(Convert.toMinecraft(rigidBody.getPhysicsRotation(new Quaternion()))));
            tag.put("linear_velocity", VectorHelper.toTag(Convert.toMinecraft(rigidBody.getLinearVelocity(new Vector3f()))));
            tag.put("angular_velocity", VectorHelper.toTag(Convert.toMinecraft(rigidBody.getAngularVelocity(new Vector3f()))));
        }
    }

    @Inject(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V"))
    public void load(CompoundTag tag, CallbackInfo info) {
        if (this instanceof EntityPhysicsElement) {
            final var rigidBody = ((EntityPhysicsElement) this).getRigidBody();
            rigidBody.setPhysicsRotation(Convert.toBullet(QuaternionHelper.fromTag(tag.getCompound("orientation"))));
            rigidBody.setLinearVelocity(Convert.toBullet(VectorHelper.fromTag(tag.getCompound("linear_velocity"))));
            rigidBody.setAngularVelocity(Convert.toBullet(VectorHelper.fromTag(tag.getCompound("angular_velocity"))));
        }
    }
}
