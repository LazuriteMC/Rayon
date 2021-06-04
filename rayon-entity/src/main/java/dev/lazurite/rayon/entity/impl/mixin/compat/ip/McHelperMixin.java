package dev.lazurite.rayon.entity.impl.mixin.compat.ip;

import com.jme3.math.Vector3f;
import dev.lazurite.rayon.core.impl.physics.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.physics.space.body.ElementRigidBody;
import dev.lazurite.rayon.core.impl.util.math.VectorHelper;
import dev.lazurite.rayon.entity.api.EntityPhysicsElement;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "com.qouteall.immersive_portals.McHelper")
public abstract class McHelperMixin {
    @Inject(method = "setPosAndLastTickPos", at = @At("HEAD"))
    private static void setPosAndLastTickPos(Entity entity, Vec3d pos, Vec3d lastTickPos, CallbackInfo info) {
        if (entity instanceof EntityPhysicsElement) {
            ElementRigidBody rigidBody = ((EntityPhysicsElement) entity).getRigidBody();
            MinecraftSpace space = MinecraftSpace.get(entity.getEntityWorld());

            space.getWorkerThread().execute(() -> {
                rigidBody.setPhysicsLocation(VectorHelper.vec3dToVector3f(pos));
                rigidBody.scheduleFrameReset();
                ((EntityPhysicsElement) entity).sendMovementUpdate(true);
            });
        }
    }

    @Inject(method = "setEyePos", at = @At("HEAD"), cancellable = true)
    private static void setEyePos(Entity entity, Vec3d eyePos, Vec3d lastTickEyePos, CallbackInfo info) {
        if (entity instanceof EntityPhysicsElement && entity.world.isClient()) {
            info.cancel();
        }
    }

    @Inject(method = "getEyePos", at = @At("HEAD"), cancellable = true)
    private static void getEyePos(Entity entity, CallbackInfoReturnable<Vec3d> info) {
        if (entity instanceof EntityPhysicsElement) {
            info.setReturnValue(VectorHelper.vector3fToVec3d(((EntityPhysicsElement) entity).getPhysicsLocation(new Vector3f(), 1.0f))
                .add(0, entity.getBoundingBox().getYLength() / 2.0, 0));
        }
    }
}