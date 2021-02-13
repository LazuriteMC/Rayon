package dev.lazurite.rayon.impl.element.hooks.entity.client;

import dev.lazurite.rayon.impl.Rayon;
import dev.lazurite.rayon.api.element.PhysicsElement;
import dev.lazurite.rayon.impl.element.ElementRigidBody;
import dev.lazurite.rayon.impl.util.math.VectorHelper;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {
    @Inject(method = "addEntity", at = @At("HEAD"))
    public void addEntity(int id, Entity entity, CallbackInfo info) {
        if (entity instanceof PhysicsElement) {
            ElementRigidBody rigidBody = ((PhysicsElement) entity).getRigidBody();
            rigidBody.setPhysicsLocation(VectorHelper.vec3dToVector3f(entity.getPos()));
            Rayon.THREAD.get(this).execute(space -> space.addCollisionObject(rigidBody));
        }
    }
}
