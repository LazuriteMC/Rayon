package dev.lazurite.rayon.entity.impl.mixin.compat.ip;

import com.jme3.math.Vector3f;
import dev.lazurite.rayon.core.impl.physics.space.body.ElementRigidBody;
import dev.lazurite.rayon.core.impl.util.math.VectorHelper;
import dev.lazurite.rayon.entity.api.EntityPhysicsElement;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "com.qouteall.immersive_portals.portal.Portal")
public class PortalMixin {
    @Shadow @Nullable public Quaternion rotation;

    @Inject(method = "transformVelocity", at = @At("HEAD"))
    public void transformVelocity(Entity entity, CallbackInfo info) {
        if (entity instanceof EntityPhysicsElement) {
            ElementRigidBody rigidBody = ((EntityPhysicsElement) entity).getRigidBody();
            Vec3f velocity = VectorHelper.bulletToMinecraft(rigidBody.getLinearVelocity(new Vector3f()));

            if (rotation != null) {
                velocity.rotate(rotation);
            }

            rigidBody.setLinearVelocity(VectorHelper.minecraftToBullet(velocity));

            // sqrt(90000) = 300 m/s speed limit
            if (rigidBody.getLinearVelocity(new Vector3f()).lengthSquared() > 90000) {
                rigidBody.setLinearVelocity(rigidBody.getLinearVelocity(new Vector3f()).normalize().multLocal(90000));
            }
        }
    }
}
