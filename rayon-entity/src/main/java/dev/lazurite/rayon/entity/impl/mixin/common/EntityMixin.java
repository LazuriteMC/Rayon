package dev.lazurite.rayon.entity.impl.mixin.common;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.core.impl.thread.space.body.ElementRigidBody;
import dev.lazurite.rayon.core.impl.util.math.QuaternionHelper;
import dev.lazurite.rayon.core.impl.util.math.VectorHelper;
import dev.lazurite.rayon.entity.api.EntityPhysicsElement;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method = "pushAwayFrom", at = @At("HEAD"), cancellable = true)
    public void pushAwayFrom(Entity entity, CallbackInfo info) {
        if (this instanceof EntityPhysicsElement && entity instanceof EntityPhysicsElement) {
            info.cancel();
        }
    }

    @Inject(
            method = "toTag",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;writeCustomDataToTag(Lnet/minecraft/nbt/CompoundTag;)V")
    )
    public void toTag(CompoundTag tag, CallbackInfoReturnable<CompoundTag> info) {
        if (this instanceof EntityPhysicsElement) {
            ElementRigidBody rigidBody = ((EntityPhysicsElement) this).getRigidBody();

            /* Movement Info */
            tag.put("orientation", QuaternionHelper.toTag(rigidBody.getPhysicsRotation(new Quaternion())));
            tag.put("linear_velocity", VectorHelper.toTag(rigidBody.getLinearVelocity(new Vector3f())));
            tag.put("angular_velocity", VectorHelper.toTag(rigidBody.getAngularVelocity(new Vector3f())));

            /* Properties */
//            tag.putFloat("drag_coefficient", rigidBody.getDragCoefficient());
//            tag.putFloat("mass", rigidBody.getMass());
//            tag.putFloat("friction", rigidBody.getFriction());
//            tag.putFloat("restitution", rigidBody.getRestitution());
        }
    }

    @Inject(
            method = "fromTag",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;readCustomDataFromTag(Lnet/minecraft/nbt/CompoundTag;)V"
            )
    )
    public void fromTag(CompoundTag tag, CallbackInfo info) {
        if (this instanceof EntityPhysicsElement) {
            if (tag.getFloat("mass") == 0.0f) return;
            ElementRigidBody rigidBody = ((EntityPhysicsElement) this).getRigidBody();

            /* Movement Info */
            rigidBody.setPhysicsRotation(QuaternionHelper.fromTag(tag.getCompound("orientation")));
            rigidBody.setLinearVelocity(VectorHelper.fromTag(tag.getCompound("linear_velocity")));
            rigidBody.setAngularVelocity(VectorHelper.fromTag(tag.getCompound("angular_velocity")));

            /* Properties */
//            rigidBody.setDragCoefficient(tag.getFloat("drag_coefficient"));
//            rigidBody.setMass(tag.getFloat("mass"));
//            rigidBody.setFriction(tag.getFloat("friction"));
//            rigidBody.setRestitution(tag.getFloat("restitution"));
            ((Entity) (Object) this).setPos(((Entity) (Object) this).getX(), ((Entity) (Object) this).getY() + ((EntityPhysicsElement) this).getRigidBody().boundingBox(new BoundingBox()).getYExtent()*3, ((Entity) (Object) this).getZ());
        }
    }
}