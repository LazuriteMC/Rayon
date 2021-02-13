package dev.lazurite.rayon.impl.element.hooks.entity.common;

import dev.lazurite.rayon.impl.Rayon;
import dev.lazurite.rayon.api.element.PhysicsElement;
import dev.lazurite.rayon.impl.element.ElementRigidBody;
import dev.lazurite.rayon.impl.util.math.VectorHelper;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {
    @Inject(method = "addEntity", at = @At("HEAD"), cancellable = true)
    private void addEntity(Entity entity, CallbackInfoReturnable<Boolean> info) {
        if (entity instanceof PhysicsElement) {
            ElementRigidBody rigidBody = ((PhysicsElement) entity).getRigidBody();
            rigidBody.setPhysicsLocation(VectorHelper.vec3dToVector3f(entity.getPos()));
            Rayon.THREAD.get(this).execute(space -> space.addCollisionObject(rigidBody));
        }
    }
}
