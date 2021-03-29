package dev.lazurite.rayon.entity.impl.mixin.compat;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.qouteall.immersive_portals.McHelper;
import dev.lazurite.rayon.core.impl.physics.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.physics.space.body.ElementRigidBody;
import dev.lazurite.rayon.core.impl.util.math.VectorHelper;
import dev.lazurite.rayon.entity.api.EntityPhysicsElement;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(McHelper.class)
public abstract class McHelperMixin {
    @Inject(method = "setPosAndLastTickPos", at = @At("HEAD"))
    private static void setPosAndLastTickPos(Entity entity, Vec3d pos, Vec3d lastTickPos, CallbackInfo info) {
        if (entity instanceof EntityPhysicsElement) {
            ElementRigidBody rigidBody = ((EntityPhysicsElement) entity).getRigidBody();
            MinecraftSpace space = MinecraftSpace.get(entity.getEntityWorld());

            space.getThread().execute(() -> {
                if (rigidBody.movementCooldown == 0) {
                    rigidBody.setPhysicsLocation(VectorHelper.vec3dToVector3f(pos)
                            .add(new Vector3f(0, rigidBody.getFrame().getBox(new BoundingBox(), 1.0f).getYExtent(), 0)));
                    rigidBody.movementCooldown = 1000000;
                    System.out.println("TP");
                } else {
                    rigidBody.movementCooldown--;
                }

                rigidBody.getFrame().set(
                        VectorHelper.vec3dToVector3f(pos),
                        VectorHelper.vec3dToVector3f(pos),
                        rigidBody.getFrame().getRotation(new Quaternion(), 0.0f),
                        rigidBody.getFrame().getRotation(new Quaternion(), 1.0f),
                        rigidBody.getFrame().getBox(new BoundingBox(), 0.0f),
                        rigidBody.getFrame().getBox(new BoundingBox(), 1.0f)
                );
            });
        }
    }

    @Inject(method = "setEyePos", at = @At("HEAD"), cancellable = true)
    private static void setEyePos(Entity entity, Vec3d eyePos, Vec3d lastTickEyePos, CallbackInfo info) {
//        if (entity instanceof EntityPhysicsElement) {
//            info.cancel();
//        }
    }
}
