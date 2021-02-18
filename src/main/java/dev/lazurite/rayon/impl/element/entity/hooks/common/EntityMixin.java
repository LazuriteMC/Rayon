package dev.lazurite.rayon.impl.element.entity.hooks.common;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.impl.Rayon;
import dev.lazurite.rayon.api.element.PhysicsElement;
import dev.lazurite.rayon.impl.bullet.body.ElementRigidBody;
import dev.lazurite.rayon.impl.bullet.world.MinecraftSpace;
import dev.lazurite.rayon.impl.element.entity.net.ElementPropertiesS2C;
import dev.lazurite.rayon.impl.element.entity.net.EntityElementMovementS2C;
import dev.lazurite.rayon.impl.util.math.QuaternionHelper;
import dev.lazurite.rayon.impl.util.math.interpolate.Frame;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow public World world;
    @Shadow public float yaw;
    @Shadow public float pitch;

    @Shadow public abstract void updatePosition(double x, double y, double z);

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo info) {
        if (this instanceof PhysicsElement) {
            PhysicsElement element = (PhysicsElement) this;
            ElementRigidBody body = element.getRigidBody();
            Frame prevFrame = body.getFrame();

            /* Update frame information for lerping */
            if (prevFrame == null) {
                body.setFrame(new Frame(
                        body.getPhysicsLocation(new Vector3f()),
                        body.getPhysicsRotation(new Quaternion())));
            } else {
                body.setFrame(new Frame(
                        prevFrame,
                        body.getPhysicsLocation(new Vector3f()),
                        body.getPhysicsRotation(new Quaternion())));
            }

            if (!world.isClient()) {
                EntityElementMovementS2C.send(element); // TODO optimize this to check for changes in position/rotation
                ElementPropertiesS2C.send(element);
            }

            Vector3f pos = body.getPhysicsLocation(new Vector3f());
            updatePosition(pos.x, pos.y, pos.z);

            yaw = QuaternionHelper.getYaw(body.getPhysicsRotation(new Quaternion()));
            pitch = QuaternionHelper.getPitch(body.getPhysicsRotation(new Quaternion()));
        }
    }

    @Inject(
            method = "toTag",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;writeCustomDataToTag(Lnet/minecraft/nbt/CompoundTag;)V"
            )
    )
    public void toTag(CompoundTag tag, CallbackInfoReturnable<CompoundTag> info) {
        if (this instanceof PhysicsElement) {
            ((PhysicsElement) this).getRigidBody().toTag(tag);
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
        if (this instanceof PhysicsElement) {
            ((PhysicsElement) this).getRigidBody().fromTag(tag);
        }
    }

    /**
     * This method cleans up after the {@link MinecraftSpace}
     * by removing any {@link ElementRigidBody}s that have had
     * their entity removed.
     * {@link Entity} removed from the world.
     */
    @Inject(method = "remove", at = @At("HEAD"))
    public synchronized void remove(CallbackInfo info) {
        if (this instanceof PhysicsElement) {
            ElementRigidBody body = ((PhysicsElement) this).getRigidBody();

            if (body.isInWorld()) {
                Rayon.THREAD.get(((Entity) (Object) this).getEntityWorld())
                        .execute(space -> space.removeCollisionObject(body));
            }
        }
    }
}
