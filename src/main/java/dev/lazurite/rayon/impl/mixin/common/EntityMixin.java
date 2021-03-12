package dev.lazurite.rayon.impl.mixin.common;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.api.element.PhysicsElement;
import dev.lazurite.rayon.impl.bullet.body.ElementRigidBody;
import dev.lazurite.rayon.impl.bullet.body.net.ElementPropertiesS2C;
import dev.lazurite.rayon.impl.bullet.body.net.ElementMovementS2C;
import dev.lazurite.rayon.impl.bullet.space.MinecraftSpace;
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
    @Shadow public abstract void updatePosition(double x, double y, double z);

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo info) {
        if (this instanceof PhysicsElement) {
            PhysicsElement element = (PhysicsElement) this;
            ElementRigidBody body = element.getRigidBody();

            /* Update frame information for lerping */
            if (body.isInWorld()) {
                Frame prevFrame = body.getFrame();

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

                /* Force the position of the entity to the rigid body's position */
                Vector3f pos = body.getPhysicsLocation(new Vector3f());
                updatePosition(pos.x, pos.y - body.boundingBox(new BoundingBox()).getYExtent(), pos.z);

                /* Send movement and property packets */
                if (!world.isClient()) {
                    if (body.getPriorityPlayer() == null && body.isActive()) {
                        ElementMovementS2C.send(element);
                    }

                    if (body.arePropertiesDirty()) {
                        ElementPropertiesS2C.send(element);
                        body.setPropertiesDirty(false);
                    }
                }
            }
        }
    }

    /**
     * Translates minecraft's "add velocity" code into "add force" code. Basically
     * calculates impulse (change in momentum) using the rigid body's mass and then
     * applies a central impulse to the object.
     * @param x velocity to add x
     * @param y velocity to add y
     * @param z velocity to add z
     * @param info required by every mixin injection
     */
    @Inject(method = "addVelocity", at = @At("HEAD"))
    public void addVelocity(double x, double y, double z, CallbackInfo info) {
        if (this instanceof PhysicsElement) {
            PhysicsElement element = (PhysicsElement) this;
            Vector3f force = new Vector3f((float) x, (float) y, (float) z).multLocal(20).multLocal(element.getRigidBody().getMass());
            MinecraftSpace.get(world).getThread().execute(() -> element.getRigidBody().applyCentralImpulse(force));
        }
    }

    @Inject(method = "pushAwayFrom", at = @At("HEAD"), cancellable = true)
    public void pushAwayFrom(Entity entity, CallbackInfo info) {
        if (this instanceof PhysicsElement && entity instanceof PhysicsElement) {
            info.cancel();
        }
    }

    @Inject(
            method = "toTag",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;writeCustomDataToTag(Lnet/minecraft/nbt/CompoundTag;)V")
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
            ((Entity) (Object) this).setPos(((Entity) (Object) this).getX(), ((Entity) (Object) this).getY() + ((PhysicsElement) this).getRigidBody().boundingBox(new BoundingBox()).getYExtent()*3, ((Entity) (Object) this).getZ());
        }
    }
}
