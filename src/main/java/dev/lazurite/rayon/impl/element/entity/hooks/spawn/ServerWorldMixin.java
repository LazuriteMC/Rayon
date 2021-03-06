package dev.lazurite.rayon.impl.element.entity.hooks.spawn;

import com.jme3.bounding.BoundingBox;
import dev.lazurite.rayon.impl.Rayon;
import dev.lazurite.rayon.api.element.PhysicsElement;
import dev.lazurite.rayon.impl.bullet.world.MinecraftSpace;
import dev.lazurite.rayon.impl.bullet.body.ElementRigidBody;
import dev.lazurite.rayon.impl.element.entity.net.EntityElementMovementS2C;
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
    @Inject(method = "loadEntityUnchecked", at = @At("TAIL"))
    private void loadEntityUnchecked(Entity entity, CallbackInfo info) {
        if (entity instanceof PhysicsElement) {
            /* Set the position of the rigid body */
            ElementRigidBody rigidBody = ((PhysicsElement) entity).getRigidBody();
            rigidBody.setPhysicsLocation(VectorHelper.vec3dToVector3f(entity.getPos().add(0, rigidBody.boundingBox(new BoundingBox()).getYExtent(), 0)));
            EntityElementMovementS2C.send((PhysicsElement) entity);

            /* Add it to the world */
            MinecraftSpace space = Rayon.SPACE.get(this);
            space.getThread().execute(() -> space.addCollisionObject(rigidBody));
        }
    }
}
