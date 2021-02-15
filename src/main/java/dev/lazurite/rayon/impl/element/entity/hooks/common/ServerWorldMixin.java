package dev.lazurite.rayon.impl.element.entity.hooks.common;

import dev.lazurite.rayon.impl.Rayon;
import dev.lazurite.rayon.api.element.PhysicsElement;
import dev.lazurite.rayon.impl.bullet.thread.MinecraftSpace;
import dev.lazurite.rayon.impl.bullet.body.ElementRigidBody;
import dev.lazurite.rayon.impl.util.math.VectorHelper;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * This mixin automatically adds entities assigned
 * {@link PhysicsElement} to the {@link MinecraftSpace}.
 */
@Mixin(ServerWorld.class)
public class ServerWorldMixin {
    @Inject(method = "loadEntityUnchecked", at = @At("HEAD"))
    private void loadEntityUnchecked(Entity entity, CallbackInfo info) {
        if (entity instanceof PhysicsElement) {
            ElementRigidBody rigidBody = ((PhysicsElement) entity).getRigidBody();
            rigidBody.setPhysicsLocation(VectorHelper.vec3dToVector3f(entity.getPos()));
            Rayon.THREAD.get(this).execute(space -> space.addCollisionObject(rigidBody));
        }
    }
}
