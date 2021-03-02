package dev.lazurite.rayon.impl.element.entity.hooks;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.impl.Rayon;
import dev.lazurite.rayon.api.element.PhysicsElement;
import dev.lazurite.rayon.impl.bullet.body.ElementRigidBody;
import dev.lazurite.rayon.impl.bullet.world.MinecraftSpace;
import dev.lazurite.rayon.impl.element.entity.net.ElementPropertiesS2C;
import dev.lazurite.rayon.impl.element.entity.net.EntityElementMovementS2C;
import dev.lazurite.rayon.impl.util.math.interpolate.Frame;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class CommonEntityMixin {
    @Shadow public int age;
    @Shadow public World world;
    @Unique private int tickCounter;
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

            /* Send movement and property packets */
            if (!world.isClient()) {
                if (body.getPriorityPlayer() == null && (age < 20 || body.getFrame().hasLocationChanged() || body.getFrame().hasRotationChanged())) {
                    EntityElementMovementS2C.send(element);
                }

                if (tickCounter > 20) {
                    ElementPropertiesS2C.send(element);
                    tickCounter = 0;
                } else {
                    ++tickCounter;
                }
            }

            /* Force the position of the entity to the rigid body's position */
            Vector3f pos = body.getPhysicsLocation(new Vector3f());
            updatePosition(pos.x, pos.y - body.boundingBox(new BoundingBox()).getYExtent(), pos.z);
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
            Rayon.THREAD.get(world).execute(space ->
                    element.getRigidBody().applyCentralImpulse(force)
            );
        }
    }

    @Inject(method = "pushAwayFrom", at = @At("HEAD"), cancellable = true)
    public void pushAwayFrom(Entity entity, CallbackInfo info) {
        if (this instanceof PhysicsElement && entity instanceof PhysicsElement) {
            info.cancel();
        }
    }

    @Inject(method = "move", at = @At("HEAD"))
    public void move(MovementType type, Vec3d movement, CallbackInfo info) {
//        if (this instanceof PhysicsElement && (type.equals(MovementType.PISTON))) {// || type.equals(MovementType.SHULKER) || type.equals(MovementType.SHULKER_BOX))) {
//            PhysicsElement element = (PhysicsElement) this;
//            Vector3f force = VectorHelper.vec3dToVector3f(movement).multLocal(20).multLocal(element.getRigidBody().getMass());
//            Rayon.THREAD.get(world).execute(space -> element.getRigidBody().applyCentralImpulse(force));
//        }
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

    /**
     * This method cleans up after the {@link MinecraftSpace}
     * by removing any {@link ElementRigidBody}s that have had
     * their entity removed.
     * {@link Entity} removed from the world.
     */
    @Inject(method = "remove", at = @At("HEAD"))
    public synchronized void remove(CallbackInfo info) {
        if (this instanceof PhysicsElement) {
            Rayon.THREAD.get(world).execute(space -> {
                ElementRigidBody body = ((PhysicsElement) this).getRigidBody();

                if (body.isInWorld()) {
                    space.removeCollisionObject(body);
                }
            });
        }
    }
}
